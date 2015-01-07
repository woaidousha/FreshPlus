package com.air.lib.communication.transaction.bean;

public class GetReportDeviceData {

    private long id;
    private long deviceId;
    private long cityId;
    private String source;
    private long createTime;
    private long modifyTime;
    private String device_uuid;
    private int status;
    private int scene;
    private int health;
    private float temperature;      //温度
    private int humidity;           //湿度
    private int pm25;              //室内PM2.5
    private int organics;           //有机物浓度
    private int aqi;                //室外空气质量
    private int indoorAqi;          //室内可比空气指数
    private int co2;
    private int delpy_pm2_5;
    private int delpy_humidity;

    public String getDevice_uuid() {
        return device_uuid;
    }

    public void setDevice_uuid(String device_uuid) {
        this.device_uuid = device_uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getOrganics() {
        return organics;
    }

    public void setOrganics(int organics) {
        this.organics = organics;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public int getIndoorAqi() {
        return indoorAqi;
    }

    public void setIndoorAqi(int indoorAqi) {
        this.indoorAqi = indoorAqi;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getDelpy_pm2_5() {
        return delpy_pm2_5;
    }

    public void setDelpy_pm2_5(int delpy_pm2_5) {
        this.delpy_pm2_5 = delpy_pm2_5;
    }

    public int getDelpy_humidity() {
        return delpy_humidity;
    }

    public void setDelpy_humidity(int delpy_humidity) {
        this.delpy_humidity = delpy_humidity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "GetReportResult{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", cityId=" + cityId +
                ", source='" + source + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", device_uuid='" + device_uuid + '\'' +
                ", status=" + status +
                ", scene=" + scene +
                ", health=" + health +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pm25=" + pm25 +
                ", organics=" + organics +
                ", aqi=" + aqi +
                ", indoorAqi=" + indoorAqi +
                ", co2=" + co2 +
                ", delpy_pm2_5=" + delpy_pm2_5 +
                ", delpy_humidity=" + delpy_humidity +
                '}';
    }
}
