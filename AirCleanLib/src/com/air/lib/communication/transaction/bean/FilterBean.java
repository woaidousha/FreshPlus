package com.air.lib.communication.transaction.bean;

import android.text.TextUtils;

public class FilterBean {

    private String filterId;
    private long usedTime;
    private long filterLife;

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public long getFilterLife() {
        return filterLife;
    }

    public void setFilterLife(long filterLife) {
        this.filterLife = filterLife;
    }

    public boolean isWorthSave() {
        return !TextUtils.isEmpty(filterId) && (filterLife > 0 || usedTime > 0);
    }

    @Override
    public String toString() {
        return "FilterBean{" +
                "filterId='" + filterId + '\'' +
                ", usedTime=" + usedTime +
                ", filterLife=" + filterLife +
                '}';
    }
}
