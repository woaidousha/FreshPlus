package com.air.board.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import com.air.board.BoardApplication;
import com.air.board.db.BoardDatabaseHelper;
import com.air.board.db.TableDeviceData;
import com.air.board.pdu.DownMotorSpeedPdu;
import com.air.board.pdu.GenericPdu;
import com.air.board.pdu.PduParser;
import com.air.board.pdu.StatusPdu;
import com.air.board.pdu.WorkModePdu;
import com.air.lib.communication.data.BasePushCmd;
import com.air.lib.communication.data.MotorSpeedRate;
import com.air.lib.communication.data.SwitchMode;
import com.air.lib.communication.transaction.bean.AirBean;
import com.air.lib.communication.transaction.bean.DeviceDataResultBean;
import com.air.lib.communication.transaction.bean.DeviceInfo;
import com.air.lib.communication.transaction.bean.DeviceUploadResultBean;
import com.air.lib.communication.transaction.bean.FilterBean;
import com.air.lib.communication.transaction.bean.MotorBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.air.lib.communication.transaction.control.Transaction;
import com.air.lib.communication.transaction.push.PushClient;
import com.air.lib.communication.transaction.push.PushMessage;
import com.air.lib.communication.transaction.push.PushMessageManager;
import com.air.lib.communication.utils.AirUtils;
import com.air.lib.communication.utils.LogTag;
import com.air.lib.communication.utils.PushSocketUtil;
import com.androidzeitgeist.ani.discovery.Discovery;
import com.androidzeitgeist.ani.discovery.DiscoveryException;
import com.androidzeitgeist.ani.discovery.DiscoveryListener;
import com.androidzeitgeist.ani.transmitter.Transmitter;
import com.androidzeitgeist.ani.transmitter.TransmitterException;
import com.example.serial.util.SerialLib;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.stmt.UpdateBuilder;
import de.greenrobot.event.EventBus;

import java.net.InetAddress;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.air.lib.communication.transaction.push.PushClient.*;

public class SerialService extends Service {

    private static final String TAG = "SerialService";

    public static final String ACTION_ALARM = "com.air.board.ACTION_ALARM";
    public static final String ACTION_SEND_DATA = "com.air.board.ACTION_SEND_DATA";
    public static final String ACTION_UPGRADE_BOARD = "com.air.board.ACTION_GRADE_BOARD";
    public static final String ACTION_WIFI_CONNECTED = "com.air.board.ACTION_WIFI_CONNECTED";

    public static final String EXTRA_SEND_PDU = "send_pdu";

    public static final int MSG_ALARM = 0;
    public static final int MSG_RECEIVE_SERIAL_DATA = 1;
    public static final int MSG_SEND_DATA = 2;
    public static final int MSG_READ_SERIAL_DATA = 3;
    public static final int MSG_UPLOAD_DATA = 4;
    public static final int MSG_NETWORK_CHANGED = 5;
    private static final int MSG_PUSH_ALARM_NOTIFY = 6;
    private static final int MSG_CONNECT_PUSH_TIMEOUT = 7;
    private static final int MSG_UPGRADE_BOARD = 8;
    private static final int MSG_BOARD_CAST_IP = 9;

    public static final int ALARM_PERIOD = 1 * 1000;
    public static final int ALARM_READ_SERIAL_PERIOD = 1 * 1000;
    public static final int ALARM_UPLOAD_DATA_PERIOD = 100 * 1000;
    public static final int READ_SERIAL_DATA_TIME_OUT = 1 * 1000;
    public static final int ALARM_PING_PERIOD = 40 * 1000;

    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private Gson mGson = new Gson();
    private static PushClient mPushClient;
    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private HashMap<String, Boolean> mControlDevices = new HashMap<String, Boolean>();
    private Discovery mDiscovery;
    private Transmitter mTransmitter;

    private ReadDataTask mReadDataTask;
    private boolean mReadingData;
    private static Boolean sUpgrading = false;

    private long mLastHeatBeatTime;
    private long mLastReadSerialTime;
    private long mLastUploadDataTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ALARM: {
                    LogTag.log(TAG, "start alarm");
                    long now = System.currentTimeMillis();
                    if (now - mLastReadSerialTime >= ALARM_READ_SERIAL_PERIOD) {
                        LogTag.log(TAG, "start read serial data");
                        mHandler.removeMessages(MSG_READ_SERIAL_DATA);
                        mHandler.sendEmptyMessage(MSG_READ_SERIAL_DATA);
                    }
                    if (!AirUtils.isWifiConnected(SerialService.this)) {
                        return;
                    }
                    if (now - mLastUploadDataTime >= ALARM_UPLOAD_DATA_PERIOD) {
                        mHandler.removeMessages(MSG_UPLOAD_DATA);
                        mHandler.sendEmptyMessage(MSG_UPLOAD_DATA);
                    }
                    if (now - mLastHeatBeatTime > ALARM_PING_PERIOD
                            || mLastHeatBeatTime == 0) {
                        mLastHeatBeatTime = now;
                        if (mPushClient != null && mPushClient.isConnecting()) {
                            LogTag.log(TAG, "connecting push");
                            return;
                        }
                        mHandler.sendEmptyMessage(MSG_PUSH_ALARM_NOTIFY);
                    }
                    break;
                }
                case MSG_READ_SERIAL_DATA: {
                    startReadData();
                    break;
                }
                case MSG_RECEIVE_SERIAL_DATA: {
                    byte[] serialData = (byte[]) msg.obj;
                    handleSerialData(serialData);
                    break;
                }
                case MSG_UPLOAD_DATA: {
                    uploadData();
                    break;
                }
                case MSG_WEBSOCKET_OPEN:
                    LogTag.log(TAG, "MSG_WEBSOCKET_OPEN");
                    removeMessages(MSG_CONNECT_PUSH_TIMEOUT);
                    shakeHandWithPushServer();
                    break;
                case MSG_WEBSOCKET_CLOSE:
                    LogTag.log(TAG, "MSG_WEBSOCKET_CLOSE");
                    break;
                case MSG_WEBSOCKET_MESSAGE:
                    LogTag.log(TAG, "MSG_WEBSOCKET_MESSAGE : " + (String) msg.obj);
                    parseMessage((String) msg.obj);
                    break;
                case MSG_WEBSOCKET_PONG:
                    LogTag.log(TAG, "MSG_WEBSOCKET_PONG");
                    mLastHeatBeatTime = System.currentTimeMillis();
                    break;
                case MSG_WEBSOCKET_ERROR:
                    LogTag.log(TAG, "MSG_WEBSOCKET_ERROR");
                    break;
                case MSG_PUSH_ALARM_NOTIFY:
                    keepAliveWithPushServer();
                    break;
                case MSG_CONNECT_PUSH_TIMEOUT:
                    closePushClient();
                    break;
                case MSG_UPGRADE_BOARD:
                    upgrade();
                    break;
                case MSG_BOARD_CAST_IP:
                    boardcastIp();
                    break;
            }
        }
    };

    private Handler mSendDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND_DATA: {
                    byte[] pduBytes = (byte[]) msg.obj;
                    LogTag.log(TAG, "send data : " + pduBytes.length);
                    for (byte pduByte : pduBytes) {
                        LogTag.log(TAG, String.format("%02x", pduByte));
                    }
                    LogTag.log(TAG, "SerialLib send data");
                    SerialLib.sendData(BoardApplication.getApplication().getSerialFd(), pduBytes);
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ensureAlarm();
        EventBus.getDefault().register(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mNetworkReceiver);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTag.log(TAG, "on start command");
        if (intent == null) {
            return Service.START_STICKY;
        }
        String action = intent.getAction();
        LogTag.log(TAG, "action : " + action);
        if (TextUtils.equals(action, ACTION_ALARM)) {
            mHandler.removeMessages(MSG_ALARM);
            mHandler.sendEmptyMessage(MSG_ALARM);
        } else if (TextUtils.equals(action, ACTION_SEND_DATA)) {
            LogTag.log(TAG, "start service send data");
            byte[] sendPdu = intent.getByteArrayExtra(EXTRA_SEND_PDU);
            sendData(sendPdu);
        } else if (TextUtils.equals(action, ACTION_UPGRADE_BOARD)) {
            mHandler.removeMessages(MSG_READ_SERIAL_DATA);
            mHandler.removeMessages(MSG_SEND_DATA);
            mHandler.removeMessages(MSG_UPGRADE_BOARD);
            mHandler.sendEmptyMessage(MSG_UPGRADE_BOARD);
        } else if (TextUtils.equals(action, ACTION_WIFI_CONNECTED)) {
            initBoardcastIp();
        }
        return Service.START_STICKY;
    }

    private void sendData(byte[] sendPdu) {
        if (sUpgrading) {
            LogTag.log(TAG, "sendData board is upgrading");
            return;
        }
        if (sendPdu == null) {
            return;
        }
        Message msg = mSendDataHandler.obtainMessage(MSG_SEND_DATA);
        msg.obj = sendPdu;
        msg.sendToTarget();
        LogTag.log(TAG, "send handle message send data");
    }

    private void ensureAlarm() {
        LogTag.log(TAG, "ensure alarm");
        mLastReadSerialTime = 0;
        mLastUploadDataTime = 0;
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        if (mAlarmIntent == null) {
            Intent intent = new Intent(ACTION_ALARM);
            mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        long triggerAtMillis = SystemClock.elapsedRealtime() + ALARM_PERIOD;
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, ALARM_PERIOD, mAlarmIntent);
    }

    private void startReadData() {
        if (sUpgrading) {
            LogTag.log(TAG, "startReadData board is upgrading");
            return;
        }
        if (BoardApplication.getApplication().getSerialFd() < 0 || mReadingData) {
            return;
        }
        mLastReadSerialTime = System.currentTimeMillis();
        mReadDataTask = new ReadDataTask();
        mReadDataTask.execute();
        mReadingData = true;
    }

    private void handleSerialData(byte[] data) {
        if (sUpgrading) {
            LogTag.log(TAG, "board is upgrading");
            return;
        }
        LogTag.log(TAG, "handle serial data : " + data);
        if (data == null || data.length == 0) {
            return;
        }
        ParseDataTask parseDataTask = new ParseDataTask();
        parseDataTask.execute(data);
    }

    public static Intent createAlarmIntent() {
        Intent intent = new Intent(BoardApplication.getApplication(), SerialService.class);
        intent.setAction(ACTION_ALARM);
        return intent;
    }

    public static Intent createUpgradeIntent() {
        Intent intent = new Intent(BoardApplication.getApplication(), SerialService.class);
        intent.setAction(ACTION_UPGRADE_BOARD);
        return intent;
    }

    private void uploadData() {
        LogTag.log(TAG, "uploadData");
        mLastUploadDataTime = System.currentTimeMillis();
        BoardDatabaseHelper helper = BoardDatabaseHelper.getInstance();
        try {
            List<TableDeviceData> datas = helper.getDeviceDataDao().queryForEq(TableDeviceData.COLUMN_UPLOAD, false);
            String deviceUuid = BoardApplication.getApplication().getBoardUuid();
            DeviceInfo deviceInfo = null;
            AirBean airBean = null;
            String content;
            Gson gson = new Gson();
            for (TableDeviceData entry : datas) {
                LogTag.log(TAG, "entry : " + entry.toString());
                content = entry.getData();
                if (TextUtils.isEmpty(content)) {
                    continue;
                }
                switch (entry.getType()) {
                    case TableDeviceData.TYPE_DEVICE_INFO: {
                        try {
                            deviceInfo = gson.fromJson(content, DeviceInfo.class);
                            Transaction.uploadDeviceInfo(BoardApplication.getApplication(), deviceInfo, entry.getId());
                        } catch (JsonSyntaxException e) {
                            LogTag.e(TAG, "parse device info data :" + content + " error.", e);
                        }
                        break;
                    }
                    case TableDeviceData.TYPE_DEVICE_DATA: {
                        try {
                            airBean = gson.fromJson(content, AirBean.class);
                            Transaction.uploadDeviceData(BoardApplication.getApplication(), deviceUuid, airBean, entry.getId());
                        } catch (JsonSyntaxException e) {
                            LogTag.e(TAG, "parse device data :" + content + " error.", e);
                        }
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onEvent(DeviceUploadResultBean response) {
        LogTag.log(TAG, "DeviceUpload response :" + response.toString());
        long id = response.getDbId();
        markAsUploaded(id);
    }

    public void onEvent(DeviceDataResultBean response) {
        LogTag.log(TAG, "DeviceData response success : " + response.toString());
        long id = response.getDbId();
        markAsUploaded(id);
    }

    private void markAsUploaded(long id) {
        BoardDatabaseHelper helper = BoardDatabaseHelper.getInstance();
        try {
            TableDeviceData deviceData = helper.getDeviceDataDao().queryForId(id);
            deviceData.setUpload(true);
            UpdateBuilder<TableDeviceData, Long> builder = helper.getDeviceDataDao().updateBuilder();
            builder.where().idEq(id);
            builder.updateColumnValue(TableDeviceData.COLUMN_UPLOAD, true);
            builder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleInitPdu() {
        Intent intent = new Intent(this, BoardService.class);
        intent.setAction(BoardService.ACTION_CREATE_WIFI_HOTSPOT);
        startService(intent);
        BoardApplication.getApplication().setNetworkStatus(PduParser.VALUE_STATUS_INITING);
        sendStatusPdu(false, true);
    }

    public static void sendStatusPdu(boolean sendBoardStatus, boolean sendNetworkStatus) {
        if (sendBoardStatus) {
            int status = BoardApplication.getApplication().getStatus();
            if (status >= PduParser.VALUE_STATUS_BOOT_COMPLETE) {
                StatusPdu statusPdu = new StatusPdu();
                statusPdu.sendPdu(BoardApplication.getApplication(), status);
            }
        }
        if (sendNetworkStatus) {
            StatusPdu statusPdu = new StatusPdu();
            int status = BoardApplication.getApplication().getNetworkStatus();
            statusPdu.sendPdu(BoardApplication.getApplication(), status);
        }
    }

    private void saveAirBean(AirBean airBean) {
        TableDeviceData tableDeviceData = new TableDeviceData();
        tableDeviceData.setTime(System.currentTimeMillis());
        Gson gson = new Gson();
        tableDeviceData.setData(gson.toJson(airBean));
        tableDeviceData.setType(TableDeviceData.TYPE_DEVICE_DATA);
        tableDeviceData.setUpload(false);
        try {
            BoardDatabaseHelper.getInstance().getDeviceDataDao().create(tableDeviceData);
        } catch (SQLException e) {
            LogTag.e(TAG, "save air bean error : " + airBean.toString(), e);
        }
    }

    private void saveDeviceInfo(DeviceInfo deviceInfo) {
        TableDeviceData tableDeviceData = new TableDeviceData();
        tableDeviceData.setTime(System.currentTimeMillis());
        Gson gson = new Gson();
        tableDeviceData.setData(gson.toJson(deviceInfo));
        tableDeviceData.setType(TableDeviceData.TYPE_DEVICE_INFO);
        tableDeviceData.setUpload(false);
        try {
            BoardApplication.getApplication().saveWorkMode(deviceInfo.getScene());
            BoardDatabaseHelper.getInstance().getDeviceDataDao().create(tableDeviceData);
        } catch (SQLException e) {
            LogTag.e(TAG, "save device info error : " + deviceInfo.toString(), e);
        }
    }

    private void saveFilterInfo(FilterBean filterBean) {
        TableDeviceData tableDeviceData = new TableDeviceData();
        tableDeviceData.setTime(System.currentTimeMillis());
        Gson gson = new Gson();
        tableDeviceData.setData(gson.toJson(filterBean));
        tableDeviceData.setType(TableDeviceData.TYPE_FILTER_DATA);
        tableDeviceData.setUpload(false);
        try {
            BoardDatabaseHelper.getInstance().getDeviceDataDao().create(tableDeviceData);
        } catch (SQLException e) {
            LogTag.e(TAG, "save filter info error : " + filterBean.toString(), e);
        }
    }

    private void saveMotorInfo(MotorBean motorBean) {
        TableDeviceData tableDeviceData = new TableDeviceData();
        tableDeviceData.setTime(System.currentTimeMillis());
        Gson gson = new Gson();
        motorBean.setWorkMode(BoardApplication.getApplication().getWorkMode());
        tableDeviceData.setData(gson.toJson(motorBean));
        tableDeviceData.setType(TableDeviceData.TYPE_MOTOR_DATA);
        tableDeviceData.setUpload(false);
        try {
            BoardDatabaseHelper.getInstance().getDeviceDataDao().create(tableDeviceData);
        } catch (SQLException e) {
            LogTag.e(TAG, "save motor info error : " + motorBean.toString(), e);
        }
    }

    class ReadDataTask extends AsyncTask<Void, Void, byte[]> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            ArrayList<byte[]> serialData = new ArrayList<byte[]>();
            long startTime = System.currentTimeMillis();
            long currentTime = System.currentTimeMillis();
            byte[] data = null;
            int length = 0;
            while (currentTime - startTime < READ_SERIAL_DATA_TIME_OUT) {
                data = SerialLib.recvData(BoardApplication.getApplication().getSerialFd());
                LogTag.log(TAG, "every data : " + (data == null ? "null" : ("data lenght : " + data.length)));
                if (data == null || data.length == 0) {
                    LogTag.log(TAG, "read data end and break");
                    break;
                }
                length += data.length;
                LogTag.log(TAG, "read now total lenght : " + length);
                serialData.add(data);
            }

            data = new byte[length];
            int position = 0;
            for (byte[] bytes : serialData) {
                System.arraycopy(bytes, 0, data, position, bytes.length);
                position += bytes.length;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (byte databyte : data) {
                stringBuilder.append(String.format("%02x", databyte));;
            }
            LogTag.log(TAG, "read datas : " + stringBuilder.toString());
            return data;
        }

        @Override
        protected void onPostExecute(byte[] s) {
            LogTag.log(TAG, "read orig data : " + s);
            LogTag.log(TAG, "read data : " + (s == null ? "null" : PduParser.toHexString(s)));
            if (s != null && s.length > 0) {
                Message msg = mHandler.obtainMessage(MSG_RECEIVE_SERIAL_DATA);
                msg.obj = s;
                msg.sendToTarget();
            }
            mReadingData = false;
        }
    }

    class ParseDataTask extends AsyncTask<byte[], Void, ArrayList<GenericPdu>> {

        @Override
        protected ArrayList<GenericPdu> doInBackground(byte[]... bytes) {
            byte[] datas = bytes[0];
            if (datas == null || datas.length == 0) {
                return null;
            }
            PduParser pduParser = new PduParser(datas);
            return pduParser.parser();
        }

        @Override
        protected void onPostExecute(ArrayList<GenericPdu> genericPdus) {
            if (genericPdus == null) {
                return;
            }
            AirBean airBean = new AirBean();
            DeviceInfo deviceInfo = new DeviceInfo();
            FilterBean filterBean = new FilterBean();
            MotorBean motorBean = new MotorBean();
            deviceInfo.setDeviceUuid(BoardApplication.getApplication().getBoardUuid());
            for (GenericPdu genericPdu : genericPdus) {
                int type = genericPdu.getType();
                switch (type) {
                    case PduParser.TYPE_REC_INIT_STATUS_CMD:
                        handleInitPdu();
                        break;
                    case PduParser.TYPE_REC_ARM_STATUS:
                        sendStatusPdu(true, true);
                        break;
                    case PduParser.TYPE_REPORT_TEMPERATURE:
                        airBean.setTemperature((Long) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_HUMIDITY:
                        airBean.setHumidity((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_PM_2_5:
                        airBean.setPm25((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_VOC:
                        airBean.setOrganics((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_LIGHT:
                        airBean.setLight((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_MOTOR1_SPEED:
                        motorBean.setMotor1Speed((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_MOTOR2_SPEED:
                        motorBean.setMotor2Speed((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_FILTER_ID:
                        filterBean.setFilterId((String) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_FILTER_USE_TIME:
                        filterBean.setUsedTime((Long) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_FILTER_LIFE:
                        filterBean.setFilterLife((Long) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_WORK_MODE_SWITCH:
                        deviceInfo.setScene((Integer) genericPdu.getResult());
                        break;
                    case PduParser.TYPE_REPORT_BOARD_ID:
                        BoardApplication.getApplication().saveBoardUuid((String) genericPdu.getResult());
                        break;
                }
            }
            if (airBean.isWorthSave()) {
                saveAirBean(airBean);
            }

            if (deviceInfo.isWorthSave()) {
                saveDeviceInfo(deviceInfo);
            }

            if (filterBean.isWorthSave()) {
                saveFilterInfo(filterBean);
            }

            if (motorBean.isWorthSave()) {
                saveMotorInfo(motorBean);
            }
        }
    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent != null ? intent.getAction() : "";

            LogTag.log(TAG, "receiver action :" + action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                connectChangeAction(context);
            }
        }

        private void connectChangeAction(Context context) {
            mHandler.removeMessages(MSG_NETWORK_CHANGED);
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            NetworkInfo.State stateWifi = null;
            if (networkInfo != null) {
                stateWifi = networkInfo.getState();
            }

            boolean hasConnect = NetworkInfo.State.CONNECTED == stateWifi;
            BoardApplication.getApplication()
                    .setNetworkStatus(hasConnect
                                      ? PduParser.VALUE_STATUS_CONNECTED_SERVER
                                      : PduParser.VALUE_STATUS_CONNECTED_SERVER_FAILED);

            if (hasConnect) {
                if (mPushClient != null) {
                    mPushClient.close();
                    mPushClient = null;
                }
                mHandler.sendEmptyMessage(MSG_PUSH_ALARM_NOTIFY);
            }
        }
    };

    private void keepAliveWithPushServer() {
        LogTag.log(TAG, "keepAliveWithPushServer " + (mPushClient == null ? " client is null "
                                                                           : ("client is opened:" + mPushClient.isOpen())));
        if (mPushClient != null && mPushClient.isOpen()) {
            try {
                LogTag.log(TAG, "ping!!!!!!!!!");
                mPushClient.ping("ping");
            } catch (Exception e) {
                LogTag.log(TAG, "ping push server error");
                e.printStackTrace();
            }
        }

        connectPushServer(APIConstant.API_PUSH_URL);
    }

    private void connectPushServer(String pushUrl) {
        LogTag.log(TAG, "mPushClient is :" + (mPushClient == null ? "null" : "not null"));
        LogTag.log(TAG, "mPushClient opened :" + (mPushClient != null && mPushClient.isOpen()  ? " opened" : "not opened"));
        if (mPushClient != null && mPushClient.isOpen()) {
            return;
        }

        if (mPushClient == null || !mPushClient.isOpen()) {
            mPushClient = new PushClient(this, URI.create(pushUrl), mHandler);
        }

        try {
            mPushClient.connect();
            mHandler.sendEmptyMessageDelayed(MSG_CONNECT_PUSH_TIMEOUT, 30 * 1000);
            LogTag.log(TAG, "mPushClient connect");
        } catch (Exception e) {
            LogTag.log(TAG, "connect push server error : ");
            e.printStackTrace();
        }
    }

    private void shakeHandWithPushServer() {
        sendStringToPush(PushSocketUtil.genShakeHandMessage(BoardApplication.getApplication().getBoardUuid()));
    }

    private void sendStringToPush(final PushMessage message) {
        synchronized (mExecutorService) {
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (mPushClient != null && mPushClient.isOpen()) {
                        if (message.getType() == PushMessage.MsgType.TYPE_SEND) {
                            PushMessageManager.getInstance().addSentMessage(message);
                        }
                        mPushClient.send(message.toJson());
                    }
                }
            });
        }
    }

    private void parseMessage(String message) {
        PushMessage pushMessage = PushMessage.fromJson(message);

        if (pushMessage == null) {
            LogTag.log(TAG, "push message is null");
            return;
        }
        LogTag.log(TAG, "push message type :" + pushMessage.getType());
        switch (pushMessage.getType()) {
            case PushMessage.MsgType.TYPE_SEND: {
                String cmdMessage = pushMessage.getParams().getMessage();
                LogTag.log(TAG, "cmdMessage : " + cmdMessage);
                try {
                    String deviceId = pushMessage.getParams().getDevice();
                    if (!TextUtils.equals(deviceId, BoardApplication.getApplication().getBoardUuid())) {
                        break;
                    }
                    BasePushCmd pushCmd = mGson.fromJson(cmdMessage, BasePushCmd.class);
                    LogTag.log(TAG, "command : " + pushCmd.toString());
                    dealPushCmd(pushCmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PushMessage respMsg = PushSocketUtil.createResponseMessage(pushMessage.getId());
                sendStringToPush(respMsg);
                break;
            }
            case PushMessage.MsgType.TYPE_RECEIVE: {
                PushMessageManager.getInstance().removeSentMessage(pushMessage.getId());
                break;
            }
            case PushMessage.MsgType.TYPE_ERROR: {
                int reason = pushMessage.getReason();
                switch (reason) {
                    case PushMessage.ResultCode.CODE_NO_DEVICE_ID:
                        shakeHandWithPushServer();
                        break;
                }
                break;
            }
        }
    }

    private void dealPushCmd(BasePushCmd cmd) {
        LogTag.log(TAG, "dealPushCmd : " + cmd.toString());
        mControlDevices.put(cmd.getSendDeviceUuid(), false);
        switch (cmd.getType()) {
            case BasePushCmd.TYPE_RES_UPDATE_MODE:
                SwitchMode switchMode = mGson.fromJson(cmd.getMsg(), SwitchMode.class);
                updateWorkMode(switchMode);
                break;
            case BasePushCmd.TYPE_RES_MOTO_SPPED:
                MotorSpeedRate rate = mGson.fromJson(cmd.getMsg(), MotorSpeedRate.class);
                DownMotorSpeedPdu pdu = new DownMotorSpeedPdu(rate.getMin(), rate.getMax());
                pdu.sendPdu(BoardApplication.getApplication());
                break;
        }
    }

    private void initBoardcastIp() {
        mDiscovery = new Discovery();
        mTransmitter = new Transmitter();
        mDiscovery.setDisoveryListener(new DiscoveryListener() {
            @Override
            public void onDiscoveryStarted() {
                mHandler.sendEmptyMessage(MSG_BOARD_CAST_IP);
            }

            @Override
            public void onDiscoveryStopped() {

            }

            @Override
            public void onDiscoveryError(Exception exception) {

            }

            @Override
            public void onIntentDiscovered(InetAddress address, String intent) {

            }
        });
        try {
            mDiscovery.enable();
        } catch (DiscoveryException e) {
            e.printStackTrace();
        }
        boardcastIp();
    }

    private void boardcastIp() {
        LogTag.log(TAG, "boardcastIp");

        new Thread() {
            @Override
            public void run() {
                try {
                    mTransmitter.transmit(PushSocketUtil.createWebSocketServerInfo().toJson());
                } catch (TransmitterException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        mHandler.sendEmptyMessageDelayed(MSG_BOARD_CAST_IP, 5000);
    }

    private void updateWorkMode(SwitchMode switchMode) {
        int mode = PduParser.VALUE_WORK_MODE_AUTO;
        switch (switchMode.getMode()) {
            case SwitchMode.CMD_MODIFY_FAST_MODE:
                mode = PduParser.VALUE_WORK_MODE_FAST;
                break;
            case SwitchMode.CMD_MODIFY_NORMAL_MODE:
                mode = PduParser.VALUE_WORK_MODE_AUTO;
                break;
            case SwitchMode.CMD_MODIFY_STANDBY_MODE:
                mode = PduParser.VALUE_WORK_MODE_STANDY;
                break;
        }
        WorkModePdu workModePdu = new WorkModePdu();
        workModePdu.sendPdu(BoardApplication.getApplication(), mode);
        DeviceInfo deviceInfo = new DeviceInfo();
        for (Map.Entry<String, Boolean> entry : mControlDevices.entrySet()) {
            LogTag.log(TAG, "send push to device : " + entry.getKey() + ", value : " + entry.getValue());
            if (entry.getValue()) {
                continue;
            }
            entry.setValue(true);
            BasePushCmd pushCmd = new BasePushCmd();
            pushCmd.setType(BasePushCmd.TYPE_RES_DEVICEINFO);
            pushCmd.setMsg(deviceInfo.getJson());
            Transaction.pushCmd(BoardApplication.getApplication(), entry.getKey(), pushCmd.getJson());
        }
    }

    private void closePushClient() {
        if (mPushClient != null) {
            mPushClient.close();
            mPushClient = null;
        }
    }

    public static void setUpgrading(Boolean upgrading) {
        SerialService.sUpgrading = upgrading;
    }

    private void upgrade() {
        sUpgrading = true;
        if (mReadDataTask != null) {
            mReadDataTask.cancel(true);
        }
        UpgradeBoardTask task = new UpgradeBoardTask();
        task.execute();
    }
}
