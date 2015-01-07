package com.air.lib.communication.data;

import com.google.gson.Gson;

public class BasePushCmd {

    public static final int TYPE_RES_WIFI_INFO = 0x00;
    public static final int TYPE_RES_WEB_SOCKET_SERVER_INFO = 0x01;
    public static final int TYPE_RES_UPDATE_MODE = 0x03;
    public static final int TYPE_RES_DEVICEINFO = 0x04;
    public static final int TYPE_RES_WIFI_LIST = 0x05;
    public static final int TYPE_RES_MOTO_SPPED = 0x06;

    public static final int TYPE_REQ_WIFI_INFO = 0X80;
    public static final int TYPE_REQ_WIFI_LIST = 0x81;

    private String sendDeviceUuid;
    private int type;
    private String msg;

    public String getSendDeviceUuid() {
        return sendDeviceUuid;
    }

    public void setSendDeviceUuid(String sendDeviceUuid) {
        this.sendDeviceUuid = sendDeviceUuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static BasePushCmd fromJson(String msg) {
        Gson gson = new Gson();
        BasePushCmd client = gson.fromJson(msg, BasePushCmd.class);
        return client;
    }

    @Override
    public String toString() {
        return "BasePushCmd{" +
                "sendDeviceUuid='" + sendDeviceUuid + '\'' +
                ", type=" + type +
                ", msg='" + msg + '\'' +
                '}';
    }
}
