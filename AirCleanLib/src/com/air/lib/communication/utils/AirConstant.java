package com.air.lib.communication.utils;

public class AirConstant {

    public static final String SELF_NAME_PHONE_PREF = "phone_";
    public static final String SELF_NAME_BOART_PREF = "board_";

    public static final String SELF_GROUP = "air_group";

    public static final String WIFI_HOTSPOT_SSID = "AIR_SSID_" + "BOARD".hashCode();
    public static final String WIFI_HOTSPOT_PWD = "123456789";
    public static final String WIFI_HOTSPOT_TYPE = "PSK";

    public static final int WEB_SOCKET_PORT = 5201;
    public static final String INIT_WEB_SOCKET_IP = "ws://192.168.43.1";
    public static final String INIT_WEB_SOCKET_HOST = INIT_WEB_SOCKET_IP + ":" + WEB_SOCKET_PORT;
    public static final String EXTRA_WEB_SOCKET_SERVER_IP = "web_socket_server_ip";
    public static final String EXTRA_WIFI_LIST = "wifi_list";
}
