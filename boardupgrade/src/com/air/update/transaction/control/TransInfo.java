package com.air.update.transaction.control;

import java.io.File;
import java.lang.reflect.Field;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.air.update.tools.Util;
import com.air.update.transaction.http.GetRequest;
import com.air.update.transaction.http.PostRequest;
import com.air.update.transaction.http.PostUploadRequest;
import com.air.update.transaction.http.Response;
import com.air.update.transaction.http.ResponseException;

public abstract class TransInfo {
    private static final String TAG = "TransInfo";

    private static final int BASE_INDEX = 0x0000;
    public static final int INDEX_LOGIN = BASE_INDEX + 1;

    public static final String KEY_DEFAULT = "";
    /**
     * 登陆
     */
    public static final String KEY_LOGIN = "login";

    public static final int HTTP_GET = 0;
    public static final int HTTP_POST = 1;
    public static final int HTTP_POST_UPLOAD_FILE = 2;
    public static final int HTTP_POST_UPLOAD_DATA = 3;
    public static final int HTTP_LOAD_PIC = 4;

    /**
     * The unique id for a transaction
     */
    // final long mTransId;

    /**
     * The callback handler for notifying UI
     */
    final Handler mClientCallback;

    /**
     * The callback intent to notifying UI
     */
    PendingIntent mCallbackIntent;

    /**
     * HTTP_GET or HTTP_POST
     */
    final int mHttpMethod;

    /**
     * Flag for clearing cache and getting data from network
     */
    boolean mRefresh = false;

    private final int mKeyIndex;

    private File mUploadFile;
    private String mUploadData;

    public TransInfo(int httpMethod, Handler handler, int keyIndex) {
        mHttpMethod = httpMethod;
        mClientCallback = handler;
        // mTransId = TransResult.generateNextId();

        this.mKeyIndex = keyIndex;
        Log.i(TAG,
                "TransInfo created, id: " + keyIndex + ", class: "
                        + this.getClass()
        );
    }

    public void setUploadFile(File file) {
        this.mUploadFile = file;
    }

    public File getUploadFile() {
        return mUploadFile;
    }

    public String getUploadData() {
        return mUploadData;
    }

    public void setUploadData(String uploadData) {
        this.mUploadData = uploadData;
    }

    public PendingIntent getCallbackIntent() {
        return mCallbackIntent;
    }

    public void setCallbackIntent(PendingIntent mCallbackIntent) {
        this.mCallbackIntent = mCallbackIntent;
    }

    public boolean isRefresh() {
        return mRefresh;
    }

    public void setRefresh(boolean refresh) {
        this.mRefresh = refresh;
    }

    public int getKeyIndex() {
        return mKeyIndex;
    }

    /**
     * Subclass need to check whether there is cache data for itself.
     *
     * @return
     */
    public boolean hasCacheData() {
        return false;
    }

    /**
     * Subclass need to clear its own cache here
     */
    public void clearCache() {
        return;
    }

    public int getHttpMethod() {
        return mHttpMethod;
    }

    public PostRequest genPostRequest() {
        throw new IllegalAccessError();
    }

    public PostUploadRequest genPostUploadRequest() {
        throw new IllegalAccessError();
    }

    public GetRequest genGetRequest() {
        throw new IllegalAccessError();
    }

    public void reportResult(ResultObj result) {
        if (mClientCallback != null) {
            Message msg = mClientCallback.obtainMessage();
            msg.obj = new TransResult(mKeyIndex, result);
            mClientCallback.sendMessageDelayed(msg, 100);
        }

        if (mCallbackIntent != null) {
            try {
                Log.i(TAG, "send pending event");
                mCallbackIntent.send(result.getResultCode());
            } catch (CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    public String getKeyByIndex(int index) {
        switch (index) {
            case INDEX_LOGIN:
                return KEY_LOGIN;
            default:
                break;
        }

        return KEY_DEFAULT;
    }

    /**
     * Parse network data and save it to the cache layer
     *
     * @param resp
     * @return
     */
    public abstract ResultObj internalProcessResponse(String resp);

    public final void handleResponse(Response resp) {
        String result = null;
        if (resp == null) {
            reportResult(new ResultObj(TransHelper.RESULT_ERR_NET_NULL_RESP, null));
            return;
        }
        try {
            result = resp.asString();
        } catch (ResponseException e) {
            e.printStackTrace();

            reportResult(new ResultObj(TransHelper.RESULT_ERR_NET_GENERAL, null));
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(result)) {
            reportResult(new ResultObj(TransHelper.RESULT_ERR_NET_NULL_RESP, null));
            return;
        }

        Util.log("Get joson string: " + result);

        reportResult(internalProcessResponse(result));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransInfo))
            return false;

        if (!this.getClass().equals(o.getClass())) {
            return false;
        }

        boolean ret = true;
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                if (Object.class.isAssignableFrom(f.getType())) {
                    if (!f.get(this).equals(f.get(o))) {
                        return false;
                    }
                }
                if (int.class.equals(f.getType())) {
                    if (f.getInt(this) != f.getInt(o)) {
                        return false;
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                ret = false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                ret = false;
            }
        }
        return ret && (mHttpMethod == ((TransInfo) o).mHttpMethod)
                && (mRefresh == ((TransInfo) o).mRefresh);
    }

}