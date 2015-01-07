package com.air.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            intent.setClass(context, UpdateService.class);
            context.startService(intent);
        }
    }
}
