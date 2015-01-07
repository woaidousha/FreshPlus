package com.air.lib.communication.transaction.bean;

public class RegisterResultBean extends ResultBean {

    RegisterResult data;

    public RegisterResult getData() {
        return data;
    }

    public void setData(RegisterResult data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RegisterResultBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}
