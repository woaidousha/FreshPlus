package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.RegisterResultBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends GsonRequest<RegisterResultBean> {

    public RegisterRequest(Response.Listener<RegisterResultBean> listener, Response.ErrorListener errorListener) {
        super(Method.POST, APIConstant.API_REGISTER_DEVICE, RegisterResultBean.class, listener, errorListener);
    }

    public RegisterRequest(Response.ErrorListener errorListener) {
        this(null, errorListener);
    }

    public RegisterRequest() {
        this(null, null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_DEVICE_UUID, mDeviceUuid);
        return map;
    }

    @Override
    protected int getRequestType() {
        return REQUEST_REGISTER;
    }
}
