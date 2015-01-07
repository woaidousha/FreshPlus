package com.air.lib.communication.data;

import com.google.gson.Gson;

public class SwitchMode {

    public static final int CMD_MODIFY_STANDBY_MODE = 1000;
    public static final int CMD_MODIFY_FAST_MODE = 1001;
    public static final int CMD_MODIFY_NORMAL_MODE = 1002;

    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public static String createPushJson(int mode, String deviceUuid) {
        Gson gson = new Gson();
        SwitchMode switchMode = new SwitchMode();
        switchMode.setMode(mode);
        BasePushCmd cmd = new BasePushCmd();
        cmd.setType(BasePushCmd.TYPE_RES_UPDATE_MODE);
        cmd.setMsg(gson.toJson(switchMode));
        cmd.setSendDeviceUuid(deviceUuid);
        return gson.toJson(cmd);
    }
}
