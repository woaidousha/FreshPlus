package com.air.lib.communication.utils;

import com.air.lib.communication.data.WifiConfig;

public interface ConnectWifiListener {

    public void onNotifyWifiConnected(WifiConfig wifiConfig);
    public void onNotifyWifiConnectFailed(WifiConfig wifiConfig);

}
