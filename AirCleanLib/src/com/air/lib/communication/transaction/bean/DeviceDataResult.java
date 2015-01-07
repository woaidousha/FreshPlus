package com.air.lib.communication.transaction.bean;

public class DeviceDataResult extends AirBean {

    private long id;
    private String deviceUuid;
    private String source;
    private long createTime;
    private int city;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "DeviceDataResult{" +
                "id=" + id +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", source='" + source + '\'' +
                ", createTime=" + createTime +
                ", city=" + city +
                "} " + super.toString();
    }
}
