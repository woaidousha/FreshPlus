package com.air.board.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import com.air.lib.communication.utils.LogTag;

public class WifiStateReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (null != parcelableExtra) {
            NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
            switch (networkInfo.getState()) {
                case CONNECTED:
                    LogTag.log(TAG,"has connected");
                    Intent i = new Intent(context, SerialService.class);
                    i.setAction(SerialService.ACTION_WIFI_CONNECTED);
                    context.startService(i);
                    break;
                case DISCONNECTED:
                    break;
            }
        }
    }
}
