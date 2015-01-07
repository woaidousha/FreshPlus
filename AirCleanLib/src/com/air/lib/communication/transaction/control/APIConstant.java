package com.air.lib.communication.transaction.control;


public class APIConstant {

    public static final String BASE_URL_HTTP = "http://182.92.79.123";

    public static final String API_PREF = BASE_URL_HTTP + "/purify/admin/";

    public static final String API_DEVICE_PREF =  API_PREF + "device/";

    public static final String API_PUSH_URL = "ws://182.92.79.123:9090/websm";

    public static final String API_REGISTER_DEVICE = API_DEVICE_PREF + "register";

    public static final String API_UPLOAD_DEVICE_INFO = API_PREF + "deviceStatus/upload";

    public static final String API_UPLOAD_DEVICE_DATA = API_PREF + "deviceData/upload";

    public static final String API_GET_REPORT = API_DEVICE_PREF + "report";

    public static final String API_ADDUSER = BASE_URL_HTTP + "/purify/register/appuser";

    public static final String API_PUSH_CMD = API_PREF + "pushCmd/push";
}
