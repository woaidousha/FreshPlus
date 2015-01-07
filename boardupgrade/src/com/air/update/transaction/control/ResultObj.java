package com.air.update.transaction.control;

public class ResultObj {
    int resultCode;
    Object obj;

    public ResultObj(int resultCode, Object obj) {
        this.resultCode = resultCode;
        this.obj = obj;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Object getObj() {
        return obj;
    }

}
