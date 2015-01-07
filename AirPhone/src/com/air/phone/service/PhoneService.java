package com.air.phone.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import com.air.lib.communication.data.BasePushCmd;
import com.air.lib.communication.transaction.bean.DeviceInfo;
import com.air.lib.communication.transaction.control.APIConstant;
import com.air.lib.communication.transaction.push.PushClient;
import com.air.lib.communication.transaction.push.PushMessage;
import com.air.lib.communication.transaction.push.PushMessageManager;
import com.air.lib.communication.utils.AirUtils;
import com.air.lib.communication.utils.LogTag;
import com.air.lib.communication.utils.PushSocketUtil;
import com.air.phone.PhoneApplication;
import com.google.gson.Gson;
import de.greenrobot.event.EventBus;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.air.lib.communication.transaction.push.PushClient.*;

public class PhoneService extends Service {

    private static final String TAG = "PhoneService";

    public static final String ACTION_ALARM = "com.air.board.ACTION_ALARM";

    public static final int ALARM_PERIOD = 1 * 1000 * 5;
    public static final int ALARM_PING_PERIOD = 1 * 1000 * 5;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private static PushClient mPushClient;
    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private Gson mGson = new Gson();

    private long mLastHeatBeatTime;

    public static final int MSG_ALARM = 0;
    private static final int MSG_MDM_ALARM_NOTIFY = 6;
    private static final int MSG_CONNECT_PUSH_TIMEOUT = 7;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ALARM: {
                    LogTag.log(TAG, "start alarm");
                    long now = System.currentTimeMillis();
                    if (!AirUtils.isWifiConnected(PhoneService.this)) {
                        return;
                    }
                    if (now - mLastHeatBeatTime > ALARM_PING_PERIOD
                            || mLastHeatBeatTime == 0) {
                        mLastHeatBeatTime = now;
                        if (mPushClient != null && mPushClient.isConnecting()) {
                            LogTag.log(TAG, "connecting push");
                            return;
                        }
                        mHandler.sendEmptyMessage(MSG_MDM_ALARM_NOTIFY);
                    }
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
                case MSG_MDM_ALARM_NOTIFY:
                    keepAliveWithPushServer();
                    break;
                case MSG_CONNECT_PUSH_TIMEOUT:
                    closePushClient();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ensureAlarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ensureAlarm() {
        LogTag.log(TAG, "ensure alarm");
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        if (mAlarmIntent == null) {
            Intent intent = new Intent(ACTION_ALARM);
            mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        long triggerAtMillis = SystemClock.elapsedRealtime() + ALARM_PERIOD;
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, ALARM_PERIOD, mAlarmIntent);
        onAlarm();
    }

    public static Intent createAlarmIntent() {
        Intent intent = new Intent(PhoneApplication.getInstance(), PhoneService.class);
        intent.setAction(ACTION_ALARM);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        }
        String action = intent.getAction();
        LogTag.log(TAG, "action : " + action);
        if (TextUtils.equals(action, ACTION_ALARM)) {
            onAlarm();
        }
        return Service.START_NOT_STICKY;
    }

    private void onAlarm() {
        mHandler.removeMessages(MSG_ALARM);
        mHandler.sendEmptyMessage(MSG_ALARM);
    }

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
        LogTag.log(TAG, "deviceUuid: " + PhoneApplication.getInstance().getPhoneUuid());
        sendStringToPush(PushSocketUtil.genShakeHandMessage(PhoneApplication.getInstance().getPhoneUuid()));
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

    private void closePushClient() {
        if (mPushClient != null) {
            mPushClient.close();
            mPushClient = null;
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
                    if (!TextUtils.equals(deviceId, PhoneApplication.getInstance().getPhoneUuid())) {
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
        switch (cmd.getType()) {
            case BasePushCmd.TYPE_RES_DEVICEINFO:
                DeviceInfo info = mGson.fromJson(cmd.getMsg(), DeviceInfo.class);
                EventBus.getDefault().post(info);
                break;
        }
    }
}
