package com.air.lib.communication.data;

public class WebSocketServerInfo {

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "WebSocketServerInfo{" +
                "ip='" + ip + '\'' +
                '}';
    }
}
