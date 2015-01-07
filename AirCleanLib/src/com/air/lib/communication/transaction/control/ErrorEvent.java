package com.air.lib.communication.transaction.control;

import com.air.lib.communication.transaction.bean.ResultBean;
import com.android.volley.VolleyError;

public class ErrorEvent {

    public ErrorEvent(int requestType, VolleyError error, ResultBean resultBean) {
        this.requestType = requestType;
        this.error = error;
        this.resultBean = resultBean;
    }

    private int requestType;
    private VolleyError error;
    private ResultBean resultBean;

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public VolleyError getError() {
        return error;
    }

    public void setError(VolleyError error) {
        this.error = error;
    }

    public ResultBean getResultBean() {
        return resultBean;
    }

    public void setResultBean(ResultBean resultBean) {
        this.resultBean = resultBean;
    }

    @Override
    public String toString() {
        return "ErrorEvent{" +
                "requestType=" + requestType +
                ", error=" + error +
                '}';
    }
}
