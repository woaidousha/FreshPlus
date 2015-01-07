package com.air.lib.communication.transaction.bean;

public class Weather {
    public long id;
    public String area;
    public int aqi;
    public int pm25;
    public int pm2524h;
    public String quality;
    public String timePoint;
    public long createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getPm2524h() {
        return pm2524h;
    }

    public void setPm2524h(int pm2524h) {
        this.pm2524h = pm2524h;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "id=" + id +
                ", area='" + area + '\'' +
                ", aqi=" + aqi +
                ", pm25=" + pm25 +
                ", pm2524h=" + pm2524h +
                ", quality='" + quality + '\'' +
                ", timePoint='" + timePoint + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
