package com.air.lib.communication.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import com.air.lib.communication.data.WifiConfig;

public class ConnectWifiHotspotTask extends AsyncTask<WifiConfig, Void, Void> {

    private static final String TAG = "ConnectWifiHotspotTask";

    private ConnectWifiListener mListener;
    private Context mContext;

    public void setListener(ConnectWifiListener listener) {
        this.mListener = listener;
    }

    public ConnectWifiHotspotTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(WifiConfig... wifiConfigs) {
        final WifiConfig wifiConfig = wifiConfigs[0];
        if (wifiConfig == null) {
            return null;
        }
        WifiAdmin wifiAdmin = new WifiAdmin(mContext) {
            @Override
            public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
                mContext.registerReceiver(receiver, filter);
                return null;
            }

            @Override
            public void myUnregisterReceiver(BroadcastReceiver receiver) {
                mContext.unregisterReceiver(receiver);
            }

            @Override
            public void onNotifyWifiConnected() {
                LogTag.log(TAG, "onNotifyWifiConnected " + wifiConfig);
                if (mListener != null) {
                    mListener.onNotifyWifiConnected(wifiConfig);
                }
            }

            @Override
            public void onNotifyWifiConnectFailed() {
                LogTag.log(TAG, "onNotifyWifiConnectFailed " + wifiConfig);
                if (mListener != null) {
                    mListener.onNotifyWifiConnectFailed(wifiConfig);
                }
            }
        };
        wifiAdmin.addNetwork(wifiConfig.SSID, wifiConfig.pwd, wifiConfig.type);
        return null;
    }

}
