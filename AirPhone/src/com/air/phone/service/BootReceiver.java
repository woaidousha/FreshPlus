package com.air.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.air.phone.PhoneApplication;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            intent.setClass(context, PhoneService.class);
            PhoneApplication.getInstance().startService(intent);
        } else if (TextUtils.equals(action, PhoneService.ACTION_ALARM)) {
            Intent alarmIntent = PhoneService.createAlarmIntent();
            PhoneApplication.getInstance().startService(alarmIntent);
        }
    }
}
