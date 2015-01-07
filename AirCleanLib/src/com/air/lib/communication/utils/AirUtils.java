package com.air.lib.communication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AirUtils {

    public static final int AQI_LEVEL_0 = 0;
    public static final int AQI_LEVEL_1 = 1;
    public static final int AQI_LEVEL_2 = 2;
    public static final int AQI_LEVEL_3 = 3;
    public static final int AQI_LEVEL_4 = 4;
    public static final int AQI_LEVEL_5 = 5;

    public static final int DANGER_AQI = AQI_LEVEL_3;

    public static final int[] AQI = {0, 50, 100, 150, 200, 300, 400, 500};
    public static final int[] PM_LEVEL = {0, 35, 75, 115, 150, 250, 350, 500};

    public static int getOutdoorAQILevel(int value) {
        if (value >= 0 && value < 51) {
            return AQI_LEVEL_0;
        } else if (value >= 51 && value < 101) {
            return AQI_LEVEL_1;
        } else if (value >= 101 && value < 151) {
            return AQI_LEVEL_2;
        } else if (value >= 151 && value < 201) {
            return AQI_LEVEL_3;
        } else if (value >= 201 && value < 301) {
            return AQI_LEVEL_4;
        } else {
            return AQI_LEVEL_5;
        }
    }

    public static int calculateAQI(int pm) {
        if (pm >= PM_LEVEL[PM_LEVEL.length - 1]) {
            return AQI[AQI.length - 1];
        }
        int result = 0;
        float highAqi = 0;
        float lowAqi = 0;
        float highPm = 0;
        float lowPm = 0;
        int highIndex = 0;
        int lowIndex = 0;
        int index = 0;
        for (index = 0; index < PM_LEVEL.length; index++) {
            if (PM_LEVEL[index] > pm) {
                highIndex = index;
                if (highIndex > 0) {
                    lowIndex = highIndex - 1;
                }
                break;
            }
        }
        highAqi = AQI[highIndex];
        lowAqi = AQI[lowIndex];
        highPm = PM_LEVEL[highIndex];
        lowPm = PM_LEVEL[lowIndex];
        if (highPm - lowPm == 0) {
            return 0;
        }
        result = (int) ((highAqi - lowAqi) / (highPm - lowPm) * (pm - lowPm) + lowAqi);
        return result;
    }

    public static boolean isDangerLevel(int level) {
        return level >= DANGER_AQI;
    }

    public static boolean getSecurity(ScanResult result) {
        if (result.capabilities.contains("WAPI-PSK")) {
            return true;
        } else if (result.capabilities.contains("WAPI-CERT")) {
            return true;
        } else if (result.capabilities.contains("WEP")) {
            return true;
        } else if (result.capabilities.contains("PSK") && !result.capabilities.contains("WAPI-PSK")) {
            return true;
        } else if (result.capabilities.contains("EAP")) {
            return true;
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager ) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected();
    }

    public static boolean isPhoneNumber(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
