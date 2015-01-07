package com.air.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class LaunchReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {     // install
            String packageName = intent.getDataString();
            if (TextUtils.equals(packageName, "com.air.board")) {
                Intent launchIntent = new Intent("com.air.board.Launch");
                context.sendBroadcast(launchIntent);
            }
            Log.i("LaunchReceiver", "安装了 :" + packageName);
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {   // uninstall
            String packageName = intent.getDataString();

            Log.i("LaunchReceiver", "卸载了 :" + packageName);
        }
    }
}
