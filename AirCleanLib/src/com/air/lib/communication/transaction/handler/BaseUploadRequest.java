package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.BaseUploadResultBean;
import com.android.volley.Response;
import de.greenrobot.event.EventBus;

import java.util.Map;

public abstract class BaseUploadRequest<T extends BaseUploadResultBean> extends GsonRequest{

    public BaseUploadRequest(int method, String url, Class clazz, Response.Listener listener, Response.ErrorListener errorListener, long dbId) {
        this(method, url, clazz, null, listener, errorListener, dbId);
    }

    public BaseUploadRequest(int method, String url, Class clazz, Response.ErrorListener errorListener, long dbId) {
        this(method, url, clazz, null, null, errorListener, dbId);
    }

    public BaseUploadRequest(int method, String url, Class clazz, long dbId) {
        this(method, url, clazz, null, null, dbId);
    }

    public BaseUploadRequest(int method, String url, Class clazz, Map headers, Response.Listener listener, Response.ErrorListener errorListener, final long dbId) {
        super(method, url, clazz, headers, listener == null ? new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (response != null && dbId > -1) {
                    response.setDbId(dbId);
                }
                EventBus.getDefault().post(response);
            }
        } : listener, errorListener);
    }
}
