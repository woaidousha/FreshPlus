package com.air.lib.communication.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogTag {

    public static final String TAG = "AIRCLEAN";

    public static final boolean ENABLE_DEBUG = true;

    public static void log(String msg) {
        log("", msg);
    }

    public static void log(String tag, String msg) {
        if (ENABLE_DEBUG) {
            StackTraceElement st = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            Log.e(tag, "[" + st.getClassName() + ":" + st.getLineNumber() + "]->" + msg);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (ENABLE_DEBUG) {
            StackTraceElement st = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            Log.e(tag, "[" + st.getClassName() + ":" + st.getLineNumber() + "]->" + msg, e);
        }
    }
}
