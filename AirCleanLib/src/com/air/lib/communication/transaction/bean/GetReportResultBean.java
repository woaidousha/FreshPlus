package com.air.lib.communication.transaction.bean;

public class GetReportResultBean extends ResultBean {

    private GetReportResultData data;
    private Weather weather;

    public GetReportResultData getData() {
        return data;
    }

    public void setData(GetReportResultData data) {
        this.data = data;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "GetReportResultBean{" +
                "data=" + data +
                ", weather=" + weather +
                "} " + super.toString();
    }
}
