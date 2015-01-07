package com.air.lib.communication.transaction.bean;

import java.util.ArrayList;

public class GetReportResultData {
    private long id;
    private String deviceUuid;
    private String token;
    private String status;
    private long registerTime;

    private ArrayList<GetReportDeviceData> dataList;
    private GetReportDeviceStatus deviceStatus;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public ArrayList<GetReportDeviceData> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<GetReportDeviceData> dataList) {
        this.dataList = dataList;
    }

    public GetReportDeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(GetReportDeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    @Override
    public String toString() {
        return "GetReportResultData{" +
                "id=" + id +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", token='" + token + '\'' +
                ", status='" + status + '\'' +
                ", registerTime=" + registerTime +
                ", dataList=" + dataList +
                ", deviceStatus=" + deviceStatus +
                '}';
    }
}
