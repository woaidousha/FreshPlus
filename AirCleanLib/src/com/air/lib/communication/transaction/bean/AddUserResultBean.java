package com.air.lib.communication.transaction.bean;

public class AddUserResultBean extends ResultBean {
    private AddUserResult data;

    public AddUserResult getData() {
        return data;
    }

    public void setData(AddUserResult data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AddUserResultBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}
