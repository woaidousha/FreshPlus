package com.air.lib.communication.transaction.bean;

public class DeviceDataResultBean extends BaseUploadResultBean {

    private DeviceDataResult data;

    public DeviceDataResult getData() {
        return data;
    }

    public void setData(DeviceDataResult data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeviceDataResultBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}
