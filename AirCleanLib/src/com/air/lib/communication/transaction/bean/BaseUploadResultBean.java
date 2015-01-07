package com.air.lib.communication.transaction.bean;

public class BaseUploadResultBean extends ResultBean {
    private long dbId;

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    @Override
    public String toString() {
        return "BaseUploadResultBean{" +
                "dbId=" + dbId +
                "} " + super.toString();
    }
}
