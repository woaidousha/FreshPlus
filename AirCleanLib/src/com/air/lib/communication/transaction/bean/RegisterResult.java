package com.air.lib.communication.transaction.bean;

public class RegisterResult {

    long id;
    String deviceUuid;
    String token;
    String salt;
    long registerTime;

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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    @Override
    public String toString() {
        return "RegisterResult{" +
                "id=" + id +
                ", deviceUuid='" + deviceUuid + '\'' +
                ", token='" + token + '\'' +
                ", salt='" + salt + '\'' +
                ", registerTime=" + registerTime +
                '}';
    }
}
