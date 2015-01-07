package com.air.update.transaction.control;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Main service for handling transactions
 */
public class TransactionService extends Service {
    public static final boolean ENABLE_LOG = true;
    public static final String LOGTAG = "TransactionService";

    private static final int MSG_STOP_SERVICE = 0;
    private static final int MSG_DELAY_TRANS = 1;

    //
    public static final int REQUEST_MAX = 3;// 目前轮寻3次
    public static final int REQUEST_TIME_OUT = 3000;// 目前轮寻间隔 2秒 2000毫秒
    // /
    public static Queue<TransInfo> mTransQueue = new LinkedList<TransInfo>();

    private NetworkThread mNetThread;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STOP_SERVICE:
                    if (ENABLE_LOG)
                        Log.v(LOGTAG, "handleMessage MSG_STOP_SERVICE: " + msg.arg1);

                    stopSelf(msg.arg1);
                    break;

                case MSG_DELAY_TRANS:
                    enqueueTransInfo((TransInfo) msg.obj);
                    break;
            }
        }
    };

    /**
     * This service always runs in the same process as its clients, we don't
     * need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public TransactionService getService() {
            if (ENABLE_LOG)
                Log.v(LOGTAG, "enter getService()");

            return TransactionService.this;
        }
    }

    @Override
    public void onCreate() {
        if (ENABLE_LOG)
            Log.v(LOGTAG, "enter onCreate()");

        mNetThread = new NetworkThread(this);
        mNetThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ENABLE_LOG)
            Log.v(LOGTAG, "enter onStartCommand()");

        Log.i(LOGTAG, "Received start id " + startId + ": " + intent);
        // TODO: need to think about when to stop the service.
        // mHandler.sendMessageDelayed(
        // Message.obtain(mHandler, MSG_STOP_SERVICE, startId, -1), 5000);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (ENABLE_LOG)
            Log.v(LOGTAG, "enter onDestroy()");

        mNetThread.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (ENABLE_LOG)
            Log.v(LOGTAG, "enter onBind()");

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (ENABLE_LOG)
            Log.v(LOGTAG, "enter onUnBind()");

        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    private boolean hasSameTransInfo(TransInfo trans) {
        // if (trans.equals(mNetThread.getCurrentTransInfo())) {
        // Log.w(LOGTAG, "hasSameTransInfo, same trans info!");
        // return true;
        // }
        // for (TransInfo ti : mTransQueue) {
        // if (ti.equals(trans)) {
        // Log.w(LOGTAG, "enqueueTransInfo, got same trans info, ignore!");
        // return true;
        // }
        // }
        return false;
    }

    private void enqueueTransInfo(TransInfo trans) {
        if (trans == null) {
            throw new IllegalArgumentException();
        }

        synchronized (mTransQueue) {
            if (hasSameTransInfo(trans))
                return;

            mTransQueue.offer(trans);
            mTransQueue.notifyAll();
        }
    }


    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = this.getPackageName();
        // Returns a list of application processes that are running on the
        // device
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            // importance:
            // The relative importance level that the system places
            // on this process.
            // May be one of IMPORTANCE_FOREGROUND, IMPORTANCE_VISIBLE,
            // IMPORTANCE_SERVICE, IMPORTANCE_BACKGROUND, or IMPORTANCE_EMPTY.
            // These constants are numbered so that "more important" values are
            // always smaller than "less important" values.
            // processName:
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
