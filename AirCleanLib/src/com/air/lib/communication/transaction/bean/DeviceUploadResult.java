package com.air.lib.communication.transaction.bean;

public class DeviceUploadResult {

    long id;
    String deviceUuid;
    int status;
    int scene;
    int health;
    long createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DeviceUploadResult{" +
                "id=" + id +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", status=" + status +
                ", scene=" + scene +
                ", health=" + health +
                ", createTime=" + createTime +
                '}';
    }
}
