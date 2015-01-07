package com.air.lib.communication.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WifiAdmin {

    private static final String TAG = "WifiAdmin";

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    // ɨ��������������б�
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;

    private WifiManager.WifiLock mWifiLock;

    private String mPasswd = "";
    private String mSSID = "";

    private Context mContext = null;

    public WifiAdmin(Context context) {

        mContext = context;

        // ȡ��WifiManager����
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // ȡ��WifiInfo����
        mWifiInfo = mWifiManager.getConnectionInfo();

        Log.v(TAG, "getIpAddress = " + mWifiInfo.getIpAddress());
    }

    // ��WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // �ر�WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public abstract Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter);

    public abstract void myUnregisterReceiver(BroadcastReceiver receiver);

    public abstract void onNotifyWifiConnected();

    public abstract void onNotifyWifiConnectFailed();

    // ���һ�����粢����
    public void addNetwork(WifiConfiguration wcg) {

        register();

        boolean isApEnable = WifiApAdmin.isWifiApEnabled(mWifiManager);
        WifiApAdmin.closeWifiAp(mContext);
        openWifi();
        if (isApEnable) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        WifiConfiguration existConfiguration = IsExsits(wcg.SSID);
        if (existConfiguration != null) {
            mWifiManager.removeNetwork(existConfiguration.networkId);
        }
        Log.d(TAG, "ssid : " + wcg.SSID );
        int wcgID = mWifiManager.addNetwork(wcg);
        Log.d(TAG, "wcgID : " + wcgID);
        if (wcgID == -1) {
            onNotifyWifiConnectFailed();
            stopTimer();
            return;
        }
        boolean connected = mWifiManager.enableNetwork(wcgID, true);
        if (connected) {
//            onNotifyWifiConnected();
        } else {
            onNotifyWifiConnectFailed();
        }
        Log.d(TAG, "connected : " + connected);
    }

    public static final int TYPE_NO_PASSWD = 0x11;
    public static final int TYPE_WEP = 0x12;
    public static final int TYPE_WPA = 0x13;

    public void addNetwork(String ssid, String passwd, int type) {
        if (ssid == null || passwd == null || ssid.equals("")) {
            Log.e(TAG, "addNetwork() ## nullpointer error!");
            return;
        }

        if (type != TYPE_NO_PASSWD && type != TYPE_WEP && type != TYPE_WPA) {
            Log.e(TAG, "addNetwork() ## unknown type = " + type);
        }

        stopTimer();
        unRegister();

        addNetwork(createWifiInfo(ssid, passwd, type));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Log.d(TAG, "RSSI changed");

                //�п��������ڻ�ȡ�������Ѿ���ȡ��
                Log.d(TAG, " intent is " + WifiManager.RSSI_CHANGED_ACTION);

                if (isWifiContected(mContext) == WIFI_CONNECTED) {
                    Log.d(TAG, "has connected");
//                    stopTimer();
//                    onNotifyWifiConnected();
//                    unRegister();
                } else if (isWifiContected(mContext) == WIFI_CONNECT_FAILED) {
//                    stopTimer();
//                    closeWifi();
//                    onNotifyWifiConnectFailed();
//                    unRegister();
                } else if (isWifiContected(mContext) == WIFI_CONNECTING) {

                }
            } else if (TextUtils.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION, intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    switch (networkInfo.getState()) {
                        case CONNECTED:
                            Log.d(TAG, "has connected");
                            stopTimer();
                            onNotifyWifiConnected();
                            unRegister();
                            break;
                        case CONNECTING:
                            Log.e("APActivity", "CONNECTING");
                            break;
                        case DISCONNECTED:
                            Log.e("APActivity", "DISCONNECTED");
                            break;
                        case DISCONNECTING:
                            Log.e("APActivity", "DISCONNECTING");
                            break;
                        case SUSPENDED:
                            Log.e("APActivity", "SUSPENDED");
                            break;
                        case UNKNOWN:
                            Log.e("APActivity", "UNKNOWN");
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };

    private final int STATE_REGISTRING = 0x01;
    private final int STATE_REGISTERED = 0x02;
    private final int STATE_UNREGISTERING = 0x03;
    private final int STATE_UNREGISTERED = 0x04;

    private int mHaveRegister = STATE_UNREGISTERED;
    private synchronized void register() {
        Log.v(TAG, "register() ##mHaveRegister = " + mHaveRegister);

        if (mHaveRegister == STATE_REGISTRING
                || mHaveRegister == STATE_REGISTERED) {
            return ;
        }

        mHaveRegister = STATE_REGISTRING;
        IntentFilter filter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        myRegisterReceiver(mBroadcastReceiver, filter);
        mHaveRegister = STATE_REGISTERED;

        startTimer();
    }

    private synchronized void unRegister() {
        Log.v(TAG, "unRegister() ##mHaveRegister = " + mHaveRegister);

        if (mHaveRegister == STATE_UNREGISTERED
                || mHaveRegister == STATE_UNREGISTERING) {
            return ;
        }

        mHaveRegister = STATE_UNREGISTERING;
        myUnregisterReceiver(mBroadcastReceiver);
        mHaveRegister = STATE_UNREGISTERED;
    }

    private Timer mTimer = null;
    private void startTimer() {
        if (mTimer != null) {
            stopTimer();
        }

        mTimer = new Timer(true);
        mTimer.schedule(mTimerTask, 120 * 1000);
    }

    private TimerTask mTimerTask = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.e(TAG, "timer out!");
            onNotifyWifiConnectFailed();
            unRegister();
        }
    };

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
            unRegister();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {

        Log.v(TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        // ��Ϊ���������1û������2��wep����3��wpa����
        if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

        } else if (type == TYPE_WEP) {  //  WIFICIPHER_WEP
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == TYPE_WPA) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    public static final int WIFI_CONNECTED = 0x01;
    public static final int WIFI_CONNECT_FAILED = 0x02;
    public static final int WIFI_CONNECTING = 0x03;
    /**
     * �ж�wifi�Ƿ����ӳɹ�,����network
     *
     * @param context
     * @return
     */
    public int isWifiContected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Log.v(TAG, "isConnectedOrConnecting = " + wifiNetworkInfo.isConnectedOrConnecting());
        Log.d(TAG, "wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());
        if (wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR
                || wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTING) {
            return WIFI_CONNECTING;
        } else if (wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return WIFI_CONNECTED;
        } else {
            Log.d(TAG, "getDetailedState() == " + wifiNetworkInfo.getDetailedState());
            return WIFI_CONNECT_FAILED;
        }
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs == null) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig == null) {
                continue;
            }
            if (existingConfig.SSID.equals("\"" + SSID + "\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {
                return existingConfig;
            }
        }
        return null;
    }



    // �Ͽ�ָ��ID������
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    // ��鵱ǰWIFI״̬
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // ��WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // ����WifiLock
    public void releaseWifiLock() {
        // �ж�ʱ����
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // ����һ��WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // �õ����úõ�����
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // ָ�����úõ������������
    public void connectConfiguration(int index) {
        // ����������úõ����������
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // �������úõ�ָ��ID������
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // �õ������б�
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // �鿴ɨ����
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // ��ScanResult��Ϣת����һ���ַ��
            // ���аѰ�����BSSID��SSID��capabilities��frequency��level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // �õ�MAC��ַ
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // �õ�������BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // �õ�IP��ַ
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // �õ����ӵ�ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // �õ�WifiInfo��������Ϣ��
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }
}
