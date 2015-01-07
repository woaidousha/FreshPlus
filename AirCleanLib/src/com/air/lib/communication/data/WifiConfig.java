package com.air.lib.communication.data;

import com.air.lib.communication.utils.WifiAdmin;
import com.google.gson.Gson;

import java.io.Serializable;

public class WifiConfig implements Serializable {

    public String SSID;
    public String pwd;
    public int type;

    public WifiConfig(String SSID, String pwd, String capabilities) {
        this.SSID = SSID;
        this.pwd = pwd;
        this.type = getSecurity(capabilities);
    }

    public static int getSecurity(String capabilities) {
        if (capabilities.contains("WEP")) {
            return WifiAdmin.TYPE_WEP;
        } else if (capabilities.contains("PSK")) {
            return WifiAdmin.TYPE_WPA;
        }
        return WifiAdmin.TYPE_NO_PASSWD;
    }

    public String getSecurity() {
        switch (type) {
            case WifiAdmin.TYPE_WEP: {
                return "WEP";
            }
            case WifiAdmin.TYPE_WPA: {
                return "PSK";
            }
            default:
                return "";
        }
    }

    public static WifiConfig fromJson(String json) {
        Gson gson = new Gson();
        WifiConfig wifiConfig = gson.fromJson(json, WifiConfig.class);
        return wifiConfig;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public WifiInfoMessage createWifiInfoMessage() {
        WifiInfoMessage wifiInfoMessage = new WifiInfoMessage();
        wifiInfoMessage.setSsid(SSID);
        wifiInfoMessage.setType(getSecurity());
        wifiInfoMessage.setPassword(pwd);
        return wifiInfoMessage;
    }

    @Override
    public String toString() {
        return "WifiConfig{" +
                "SSID='" + SSID + '\'' +
                ", pwd='" + pwd + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
