package com.air.update;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.air.update.tools.Util;
import com.air.update.transaction.bean.CheckItem;
import com.air.update.transaction.bean.CheckResponse;
import com.google.gson.Gson;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;

public class UpdateService extends Service {

    private static final String TAG = "UpdateService";

    public static final String ACTION_SERVICE = "com.air.update.service";

    private static final int MSG_CHECK_COMPLETE = 1;
    private static final int MSG_DOWNLOAD_COMPLETE = 2;
    private static final int MSG_LAUNCH_BOARD = 3;

    private static final long DELAY_TIME = 5000;
    private String mDowningVersion;
    private boolean mRunning = false;
    private boolean mIsChecking = false;
    private int mNowVersion;
    private Gson gson = new Gson();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECK_COMPLETE:
                    Log.d(TAG, "response :" + msg.obj.toString());
                    CheckResponse response = gson.fromJson(msg.obj.toString(), CheckResponse.class);
                    CheckItem item = response.getData();
                    if (mNowVersion != item.getVersionCode()) {
                        mNowVersion = item.getVersionCode();
                        download(item.getSourceUrl(), item.getVersionCode() + ".apk");
                    } else {
                        checkVision(true);
                    }
                    break;
                case MSG_DOWNLOAD_COMPLETE:
                    File file = (File) msg.obj;
                    Log.d(TAG, "file path :" + file.getAbsolutePath());
                    PackageUtils.install(UpdateService.this, file.getAbsolutePath());
                    sendEmptyMessageDelayed(MSG_LAUNCH_BOARD, 10000);
                    file.delete();
                    checkVision(false);
//                    installApk(file);
                    break;
                case MSG_LAUNCH_BOARD: {
                    Log.d(TAG, "launch board application");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.air.board", "com.air.board.ui.MainActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
            checkVision(false);
        }
        mNowVersion = getAppVersionName(this);
        return START_STICKY;
    }

    private int getAppVersionName(Context context) {
        int versionName = -1;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    "com.air.board", 0);
            versionName = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void checkVision(boolean delay) {
        long delayMillis = delay ? DELAY_TIME : 0;
        if (mIsChecking)
            return;
        mIsChecking = true;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Util.log("check thread begin !!!  ");

                Future<String> loading = Ion.with(UpdateService.this)
                        .load("http://182.92.79.123/purify/admin/appVersion/check/android?packageName=com.air.board")
                        .asString().setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                mIsChecking = false;
                                try {
                                    if (e != null) {
                                        throw e;
                                    }
                                    Message message = mHandler.obtainMessage(MSG_CHECK_COMPLETE);
                                    message.obj = result;
                                    mHandler.sendMessage(message);
                                } catch (Exception ex) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Util.log("check thread end !!!");
            }
        }, delayMillis);
    }

    private void download(String url, String name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), name);
        Future<File> downloading = Ion
                .with(UpdateService.this)
                .load(url)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        Log.d(TAG, downloaded + " / " + total);
                    }
                })
                        // write to a file
                .write(file)
                        // run a callback on completion
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        try {
                            if (e != null) {
                                throw e;
                            }
                            Message message = mHandler.obtainMessage(MSG_DOWNLOAD_COMPLETE);
                            message.obj = result;
                            mHandler.sendMessage(message);
                        } catch (Exception ex) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
