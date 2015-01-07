/* ---------------------------------------------------------------------------------------------
 *                       Copyright (c) 2013 Capital Alliance Software(Pekall) 
 *                                    All Rights Reserved
 *    NOTICE: All information contained herein is, and remains the property of Pekall and
 *      its suppliers,if any. The intellectual and technical concepts contained herein are
 *      proprietary to Pekall and its suppliers and may be covered by P.R.C, U.S. and Foreign
 *      Patents, patents in process, and are protected by trade secret or copyright law.
 *      Dissemination of this information or reproduction of this material is strictly 
 *      forbidden unless prior written permission is obtained from Pekall.
 *                                     www.pekall.com
 *--------------------------------------------------------------------------------------------- 
*/

package com.air.update;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;

import com.air.update.tools.Util;
import com.air.update.transaction.control.NetworkThread;

public class UpdateApp extends Application {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Util.log("----onCreate-----");
        if (!NetworkThread.getThreadInstance(this).isRunning()) {
            NetworkThread.getThreadInstance(this).start();
        }
    }


    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = this.getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public interface UpdateCallback {
        void executeAfterUpdate();
    }

    @Override
    public void onTerminate() {
        Util.log("----app onTerminate-----");
        super.onTerminate();
    }

}
