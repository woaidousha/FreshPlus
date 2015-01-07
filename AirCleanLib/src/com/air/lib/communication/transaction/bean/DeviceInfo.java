package com.air.lib.communication.transaction.bean;

import android.text.TextUtils;
import com.google.gson.Gson;

public class DeviceInfo {

    private String deviceUuid;
    private int status = -1;
    private int scene = -1;
    private int health = -1;

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isWorthSave() {
        return !TextUtils.isEmpty(deviceUuid) && (status != -1 || scene != -1 || health != -1);
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
