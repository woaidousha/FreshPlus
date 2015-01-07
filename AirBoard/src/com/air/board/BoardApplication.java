package com.air.board;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import com.air.board.pdu.PduParser;
import com.air.board.service.BoardService;
import com.air.board.service.SerialService;
import com.air.board.util.Constant;
import com.air.lib.communication.utils.LogTag;
import com.example.serial.util.SerialLib;

public class BoardApplication extends Application {

    private static final String TAG = "BoardApplication";

    public static boolean DEBUG = true;

    private static BoardApplication sApplication;
    private int mFd;

    private int mStatus = PduParser.VALUE_STATUS_BOOT_COMPLETE;
    private int mNetworkStatus = PduParser.VALUE_STATUS_CONNECTED_SERVER_FAILED;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        saveVersion();
        initSerial();
        Intent intent = new Intent(this, BoardService.class);
        startService(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        closeSerial();
    }

    public static BoardApplication getApplication() {
        return sApplication;
    }

    private void saveVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        int version = -1;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.PREF_KEY_VERSION, version);
        editor.commit();
    }

    public void saveBoardUuid(String uuid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.PREF_KEY_UUID, uuid);
        editor.commit();
    }

    public String getBoardUuid() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(Constant.PREF_KEY_UUID, "");
    }

    private void initSerial() {
        mFd = SerialLib.initSerial();
        LogTag.log(TAG, "init serial mFd :" + mFd);
        Intent intent = new Intent(this, SerialService.class);
        startService(intent);
    }

    private void closeSerial() {
        LogTag.log(TAG, "close serial mFd :" + mFd);
        SerialLib.closeSerial(mFd);
    }

    public int getSerialFd() {
        return mFd;
    }

    public String getBoardname() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(Constant.PREF_SELF_NAME, "");
    }

    public boolean hasRegister() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(Constant.PREF_BOARD_HAS_REGISTERED, false);
    }

    public void updateRegister(boolean hasRegistered) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(Constant.PREF_BOARD_HAS_REGISTERED, hasRegistered).commit();
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getNetworkStatus() {
        return mNetworkStatus;
    }

    public void setNetworkStatus(int networkStatus) {
        this.mNetworkStatus = networkStatus;
    }

    public void startSendDataService(byte[] pdu) {
        Intent intent = new Intent(BoardApplication.getApplication(), SerialService.class);
        intent.setAction(SerialService.ACTION_SEND_DATA);
        intent.putExtra(SerialService.EXTRA_SEND_PDU, pdu);
        startService(intent);
    }

    public void saveWorkMode(int workMode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt(Constant.PREF_WORK_MODE, workMode).commit();
    }

    public int getWorkMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt(Constant.PREF_WORK_MODE, -1);
    }
}
