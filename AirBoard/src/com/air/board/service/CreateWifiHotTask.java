package com.air.board.service;

import android.os.AsyncTask;
import com.air.board.BoardApplication;
import com.air.lib.communication.utils.AirConstant;
import com.air.lib.communication.utils.WifiApAdmin;

public class CreateWifiHotTask extends AsyncTask<Void, Void, Void> {

    private WifiApAdmin.CreateApResultListener mCreateApResultListener;

    public void setCreateApResultListener(WifiApAdmin.CreateApResultListener createApResultListener) {
        this.mCreateApResultListener = createApResultListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        WifiApAdmin apAdmin = new WifiApAdmin(BoardApplication.getApplication());
        apAdmin.setCreateApResultListener(mCreateApResultListener);
        apAdmin.startWifiAp(AirConstant.WIFI_HOTSPOT_SSID, AirConstant.WIFI_HOTSPOT_PWD);
        return null;
    }
}
