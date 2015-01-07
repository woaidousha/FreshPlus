package com.air.lib.communication.transaction.bean;

public class ResultBean {

    int result;
    int errorCode;
    String msg;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "result=" + result +
                ", errorCode=" + errorCode +
                ", msg='" + msg + '\'' +
                '}';
    }
}
