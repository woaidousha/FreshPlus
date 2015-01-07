package com.air.board.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.air.board.BoardApplication;
import com.air.board.pdu.PduParser;
import com.air.board.util.Constant;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.data.WifiInfoMessage;
import com.air.lib.communication.transaction.bean.RegisterResultBean;
import com.air.lib.communication.transaction.cache.StateAndErrorCode;
import com.air.lib.communication.transaction.control.ErrorEvent;
import com.air.lib.communication.transaction.control.Transaction;
import com.air.lib.communication.transaction.handler.GsonRequest;
import com.air.lib.communication.utils.AirConstant;
import com.air.lib.communication.utils.AirUtils;
import com.air.lib.communication.utils.ConnectWifiHotspotTask;
import com.air.lib.communication.utils.ConnectWifiListener;
import com.air.lib.communication.utils.LogTag;
import com.air.lib.communication.utils.WifiApAdmin;
import com.google.gson.Gson;
import de.greenrobot.event.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BoardService extends Service implements WifiApAdmin.CreateApResultListener,
        ConnectWifiListener, InitSocketServer.OnWifiConfigListener {

    private static final String TAG = "BoardService";

    public static final String ACTION_CREATE_WIFI_HOTSPOT = "com.air.board.ACTION_CREATE_WIFI_HOTSPOT";
    public static final String ACTION_WIFI_CONNECTED = "com.air.board.ACTION_WIFI_CONNECTED";

    public static final int MSG_CREATE_WIFI_HOTSPOT = 0;
    public static final int MSG_INIT_SOCKET = 1;
    public static final int MSG_INIT_NET_THREAD = 4;
    public static final int MSG_BOOT_COMPLETE = 5;
    public static final int MSG_SCAN_HOME_WIFI = 6;

    SharedPreferences mPreferences;
    public CreateWifiHotTask mCreateWifiHotTask;
    private int mCreateCount = 0;

    private WifiManager mWifiManager;
    private WifiReceiver mWifiReceiver;
    private ArrayList<ScanResult> mScanResults;
    private InitSocketServer mInitSocketServer;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CREATE_WIFI_HOTSPOT: {
                    if (mCreateCount++ > 5) {
                        LogTag.log(TAG, "create wifi spot failed!");
                        return;
                    }
                    createWifiHotspot();
                    break;
                }
                case MSG_INIT_SOCKET: {
                    initSocketServer();
                    break;
                }
                case MSG_BOOT_COMPLETE: {
                    sendBootPdu();
                    connectWifiOnBoot();
                    break;
                }

                case MSG_SCAN_HOME_WIFI: {
                    startScanWifi();
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        connectWifiOnBoot();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        }
        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_CREATE_WIFI_HOTSPOT)) {
            mCreateCount = 0;
            mHandler.removeMessages(MSG_SCAN_HOME_WIFI);
            mHandler.sendEmptyMessage(MSG_SCAN_HOME_WIFI);
        } else if (TextUtils.equals(action, ACTION_WIFI_CONNECTED)) {
            mHandler.removeMessages(MSG_INIT_NET_THREAD);
            mHandler.sendEmptyMessageDelayed(MSG_INIT_NET_THREAD, 1500);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            mHandler.removeMessages(MSG_BOOT_COMPLETE);
            mHandler.sendEmptyMessage(MSG_BOOT_COMPLETE);
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reset();
    }

    private void reset() {
        mHandler.removeMessages(MSG_INIT_SOCKET);
        mHandler.removeMessages(MSG_CREATE_WIFI_HOTSPOT);
        closeSocketServer();
    }

    private void createWifiHotspot() {
        LogTag.log(TAG, "createWifiHotspot");
        reset();
        if (mCreateWifiHotTask != null) {
            mCreateWifiHotTask.cancel(true);
        }
        mPreferences.edit().putBoolean(Constant.PREF_HAS_STORE_HOME_WIFI, false);
        mCreateWifiHotTask = new CreateWifiHotTask();
        mCreateWifiHotTask.setCreateApResultListener(this);
        mCreateWifiHotTask.execute();
    }

    @Override
    public void createApResult(int result) {
        LogTag.log(TAG, "create ap reslut :" + result);
        if (WifiApAdmin.RESULT_CREATED == result) {
            mHandler.sendEmptyMessage(MSG_INIT_SOCKET);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_CREATE_WIFI_HOTSPOT, 5000);
        }
    }

    private void initSocketServer() {
        if (mInitSocketServer != null) {
            try {
                mInitSocketServer.stop();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mInitSocketServer = null;
        }
        try {
            mInitSocketServer = new InitSocketServer(this);
            mInitSocketServer.setWifiConfigListener(this);
            mInitSocketServer.start();
        } catch (UnknownHostException e) {
            LogTag.e(TAG, "initSocketServer error ", e);
            initSocketServer();
            return;
        }
    }

    private void closeSocketServer() {
        try {
            if (mInitSocketServer != null) {
                mInitSocketServer.stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotifyWifiConnected(WifiConfig wifiConfig) {
        LogTag.log(TAG, "connect wifi success");
        BoardApplication.getApplication().setNetworkStatus(PduParser.VALUE_STATUS_CONNECTED_SERVER);
        SerialService.sendStatusPdu(false, true);
        mPreferences.edit().putBoolean(Constant.PREF_HAS_STORE_HOME_WIFI, true).commit();
        closeSocketServer();
        registerDevice();
    }

    @Override
    public void onNotifyWifiConnectFailed(WifiConfig wifiConfig) {
        LogTag.log(TAG, "connect wifi failed");
        BoardApplication.getApplication().setNetworkStatus(PduParser.VALUE_STATUS_CONNECTED_SERVER_FAILED);
        SerialService.sendStatusPdu(false, true);
    }

    private void registerDevice() {
        if (!BoardApplication.getApplication().hasRegister()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            Transaction.register(BoardApplication.getApplication(), BoardApplication.getApplication().getBoardUuid());
        }
    }

    public void onEvent(RegisterResultBean registerResultBean) {
        EventBus.getDefault().unregister(this);
        if (registerResultBean.getResult() == StateAndErrorCode.OPERATOR_SUCCESS
                && registerResultBean.getData() != null) {
            BoardApplication.getApplication().updateRegister(true);
        } else {
            registerDevice();
        }
    }

    public void onEvent(ErrorEvent errorEvent) {
        EventBus.getDefault().unregister(this);
        if (errorEvent.getRequestType() == GsonRequest.REQUEST_REGISTER) {
            registerDevice();
        }
    }

    private void connectWifi(WifiInfoMessage message) {
        WifiConfig wifiConfig = new WifiConfig(message.getSsid(), message.getPassword(), message.getType());
        ConnectWifiHotspotTask task = new ConnectWifiHotspotTask(BoardApplication.getApplication());
        task.setListener(this);
        task.execute(wifiConfig);
    }

    private void connectWifiOnBoot() {
        String wifi = mPreferences.getString(Constant.PREF_HOME_WIFI_INFO, "");

        if (BoardApplication.DEBUG) {
            AssetManager assetManager = getAssets();
            InputStream inputStream = null ;
            try {
                inputStream = assetManager.open("wifi_info");
                wifi = readTextFile(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LogTag.log(TAG, "wifi : " + wifi);
        if (TextUtils.isEmpty(wifi)) {
            return;
        }
        if (AirUtils.isWifiConnected(this)) {
            return;
        }
        LogTag.log(TAG, "connectWifiOnBoot" );
        Gson gson = new Gson();
        WifiInfoMessage message = gson.fromJson(wifi, WifiInfoMessage.class);
        connectWifi(message);
    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return outputStream.toString();
    }

    private void startScanWifi() {
        LogTag.log(TAG, "startScanWifi");
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        }
        if (mWifiReceiver == null) {
            mWifiReceiver = new WifiReceiver();
        }
        WifiApAdmin.closeWifiAp(this);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiReceiver, filter);
        boolean flag = mWifiManager.startScan();
        if (!flag) {
            startScanWifi();
        }
    }

    private void sendBootPdu() {
        BoardApplication.getApplication().setStatus(PduParser.VALUE_STATUS_BOOT_COMPLETE);
        SerialService.sendStatusPdu(true, false);
    }

    @Override
    public void onWifiConfig(WifiConfig config) {
        final WifiInfoMessage infoMessage = config.createWifiInfoMessage();
        mPreferences.edit().putString(Constant.PREF_HOME_WIFI_INFO, infoMessage.getJsonString()).apply();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                connectWifi(infoMessage);
            }
        };
        timer.schedule(task, 3000);
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogTag.log(TAG, "receive wifi scan results");
            unregisterReceiver(mWifiReceiver);
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            updateWifiSSID(scanResults);
            if (mScanResults.size() == 0) {
                startScanWifi();
            } else {
                mHandler.removeMessages(MSG_CREATE_WIFI_HOTSPOT);
                mHandler.sendEmptyMessage(MSG_CREATE_WIFI_HOTSPOT);
            }
        }

        private void updateWifiSSID(List<ScanResult> scanResults) {
            HashMap<String, ScanResult> map = new HashMap<String, ScanResult>();
            for (ScanResult scanResult : scanResults) {
                LogTag.log(TAG, "scan result : " + scanResult.toString());
                if (TextUtils.isEmpty(scanResult.SSID)
                        || scanResult.SSID.startsWith(AirConstant.WIFI_HOTSPOT_SSID)) {
                    continue;
                }
                if (TextUtils.equals("00:00:00:00:00:00", scanResult.BSSID)) {
                    continue;
                }
                ScanResult result = map.get(scanResult.SSID);
                if (result != null && result.level < scanResult.level) {
                    continue;
                }
                map.put(scanResult.SSID, scanResult);
            }
            if (mScanResults == null) {
                mScanResults = new ArrayList<ScanResult>();
            } else {
                mScanResults.clear();
            }
            for (ScanResult scanResult : map.values()) {
                mScanResults.add(scanResult);
            }
            Collections.sort(mScanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult scanResult, ScanResult scanResult2) {
                    if (scanResult.level < scanResult2.level) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            LogTag.log(TAG, Arrays.toString(mScanResults.toArray()));
        }
    }

    public ArrayList<ScanResult> getScanResults() {
        return mScanResults;
    }
}
