package com.air.lib.communication.transaction.bean;

public class GetReportDeviceStatus {

    private long id;
    private String deviceUuid;
    private int status;
    private int scene;
    private int health;

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

    @Override
    public String toString() {
        return "GetReportDeviceStatus{" +
                "id=" + id +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", status=" + status +
                ", scene=" + scene +
                ", health=" + health +
                '}';
    }
}
