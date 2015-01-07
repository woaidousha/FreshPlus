package com.air.lib.communication.transaction.bean;

public class AirBean {

    private float temperature;      //温度
    private int humidity;           //湿度
    private int pm25;              //室内PM2.5
    private int organics;           //有机物浓度
    private int aqi;                //室外空气质量
    private int indoorAqi;          //室内可比空气指数
    private int co2;
    private int light;

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

    public boolean isWorthSave() {
        return temperature != 0 || humidity != 0 || organics != 0 || aqi != 0 || indoorAqi != 0 || co2 != 0;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    @Override
    public String toString() {
        return "AirBean{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", pm25=" + pm25 +
                ", organics=" + organics +
                ", aqi=" + aqi +
                ", indoorAqi=" + indoorAqi +
                '}';
    }
}
