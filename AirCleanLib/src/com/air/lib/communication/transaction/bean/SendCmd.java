package com.air.lib.communication.transaction.bean;

public class SendCmd {

    private String deviceUuid;
    private int cmd;
    private String content;

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SendCmdBean{" +
                "deviceUuid='" + deviceUuid + '\'' +
                ", cmd=" + cmd +
                ", content='" + content + '\'' +
                '}';
    }
}
