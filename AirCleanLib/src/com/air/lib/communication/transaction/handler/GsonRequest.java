package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.ResultBean;
import com.air.lib.communication.transaction.control.ErrorEvent;
import com.air.lib.communication.utils.LogTag;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.greenrobot.event.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class GsonRequest<T> extends Request<T> implements Response.Listener<T> {

    private static final String TAG = "GsonRequest";

    public static final String PARAM_DEVICE_UUID = "deviceUuid";

    public static final int REQUEST_REGISTER = 0x1001;
    public static final int REQUEST_DEVICE_INFO_UPLOAD = 0x1002;
    public static final int REQUEST_DEVICE_DATA_UPLOAD = 0x1003;
    public static final int REQUEST_GETREPORT = 0x1004;
    public static final int REQUEST_ADD_USER = 0x1005;
    public static final int REQUEST_PUSH_CMD = 0x1006;

    private final Gson mGson = new Gson();
    private final Class<T> mClazz;
    private final Response.Listener<T> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;
    protected String mDeviceUuid;

    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, url, clazz, null, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz, Response.ErrorListener errorListener) {
        this(method, url, clazz, null, null, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz) {
        this(method, url, clazz, null, null, null);
    }

    public GsonRequest(int method, String url,
                       Class<T> clazz,
                       Map<String, String> headers,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, null);
        this.mClazz = clazz;
        this.mHeaders = headers;
        if (listener != null) {
            this.mListener = listener;
        } else {
            this.mListener = this;
        }
        mErrorListener = errorListener;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.mDeviceUuid = deviceUuid;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        LogTag.e("", "request error :", error);
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
        ErrorEvent errorEvent = new ErrorEvent(getRequestType(), error, null);
        EventBus.getDefault().post(errorEvent);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            LogTag.log(TAG, "response : " + json);
            T result = mGson.fromJson(json, mClazz);
            if (result == null) {
                EventBus.getDefault().post(null);
            } else {
                if (((ResultBean) result).getResult() != 0) {
                    ErrorEvent errorEvent = new ErrorEvent(getRequestType(), null, ((ResultBean) result));
                    EventBus.getDefault().post(errorEvent);
                    return null;
                }
            }
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = super.getBody();
        if (body == null) {
            LogTag.log(TAG, "body is null");
        } else {
            LogTag.log(TAG, "body is " + new String(body));
        }
        return body;
    }

    @Override
    public void onResponse(T response) {
        LogTag.log(TAG, "response : " + response.getClass().getName());
        EventBus.getDefault().post(response);
    }

    protected abstract int getRequestType();
}