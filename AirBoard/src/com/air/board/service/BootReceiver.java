package com.air.board.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.air.board.BoardApplication;
import com.air.lib.communication.utils.LogTag;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogTag.log("BootReceiver", "action : " + action);
        if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            intent.setClass(context, BoardService.class);
            BoardApplication.getApplication().startService(intent);
        } else if (TextUtils.equals(action, SerialService.ACTION_ALARM)) {
            Intent alarmIntent = SerialService.createAlarmIntent();
            BoardApplication.getApplication().startService(alarmIntent);
        } else if (TextUtils.equals(action, "com.air.board.Launch")) {
            Intent alarmIntent = SerialService.createAlarmIntent();
            BoardApplication.getApplication().startService(alarmIntent);
        }
    }
}
