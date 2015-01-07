package com.air.update.transaction.bean;

public class CheckResponse extends Response{

    CheckItem data;

    public CheckItem getData() {
        return data;
    }

    public void setData(CheckItem data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CheckResponse{" +
                "data=" + data +
                "} " + super.toString();
    }
}
