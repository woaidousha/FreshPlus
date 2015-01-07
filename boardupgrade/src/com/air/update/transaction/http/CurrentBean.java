package com.air.update.transaction.http;

public class CurrentBean {

    int result;//":"1","
    int error;//":"20003"}

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "CurrentBean [result=" + result + ", error=" + error + "]";
    }


}
