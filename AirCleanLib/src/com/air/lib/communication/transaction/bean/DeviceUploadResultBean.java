package com.air.lib.communication.transaction.bean;

public class DeviceUploadResultBean extends BaseUploadResultBean {

    private DeviceUploadResult data;

    public DeviceUploadResult getData() {
        return data;
    }

    public void setData(DeviceUploadResult data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeviceUploadResultBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}
