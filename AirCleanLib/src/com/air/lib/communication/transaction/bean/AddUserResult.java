package com.air.lib.communication.transaction.bean;

public class AddUserResult {

    private String loginName;
    private String token;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AddUserResult{" +
                "loginName='" + loginName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
