package com.air.lib.communication.data;

import android.net.wifi.ScanResult;

import java.util.ArrayList;

public class WifiInfoList {
    private ArrayList<ScanResult> scanResults;

    public ArrayList<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(ArrayList<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }
}
