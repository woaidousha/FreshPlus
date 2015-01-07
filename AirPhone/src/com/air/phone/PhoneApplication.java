package com.air.phone;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.utils.AirConstant;
import com.air.phone.service.PhoneService;
import com.air.phone.util.Constant;

import java.util.Random;
import java.util.UUID;

public class PhoneApplication extends Application {

    private WifiConfig mWifiConfig;

    private static PhoneApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ensureUuid();
        startPhoneService();
    }

    public static PhoneApplication getInstance() {
        return sInstance;
    }

    private void startPhoneService() {
        Intent intent = new Intent(this, PhoneService.class);
        startService(intent);
    }

    public void setWifiConfig(WifiConfig wifiConfig) {
        this.mWifiConfig = wifiConfig;
    }

    public void saveHomeWifi(WifiConfig config) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(Constant.PREF_KEY_HOME_WIFI, config.toJson());
    }

    public WifiConfig getWifiConfig() {
        return mWifiConfig;
    }

    private void ensureUuid() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains(Constant.PREF_KEY_UUID)) {
            Random random = new Random();
            UUID uuid = new UUID(random.nextLong(), System.currentTimeMillis());
            SharedPreferences.Editor editor = preferences.edit();
            String uuidString = uuid.toString();
            editor.putString(Constant.PREF_KEY_UUID, uuidString);
            editor.putString(Constant.PREF_SELF_NAME, AirConstant.SELF_NAME_PHONE_PREF + uuidString);
            editor.commit();
        }
    }

    public String getPhoneName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(Constant.PREF_SELF_NAME, "");
    }

    public void saveBoardUuid(String uuid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(Constant.PREF_KEY_BOARD_UUID, uuid).commit();
    }

    public String getBoardUuid() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(Constant.PREF_KEY_BOARD_UUID, null);
    }

    public void saveUsername(String username, String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(Constant.PREF_KEY_USERNAME, username)
                .putString(Constant.PREF_KEY_TOKEN, token).commit();
    }

    public boolean hasLogined() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString(Constant.PREF_KEY_USERNAME, "");
        String token = preferences.getString(Constant.PREF_KEY_TOKEN, "");
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(token);
    }

    public String getPhoneUuid() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(Constant.PREF_KEY_UUID, "");
    }

}
