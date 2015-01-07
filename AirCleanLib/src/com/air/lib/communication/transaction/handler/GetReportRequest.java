package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.GetReportResultBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.air.lib.communication.utils.LogTag;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class GetReportRequest extends GsonRequest<GetReportResultBean> {

    public GetReportRequest(Response.Listener<GetReportResultBean> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, APIConstant.API_GET_REPORT, GetReportResultBean.class, listener, errorListener);
    }

    public GetReportRequest(Response.ErrorListener errorListener) {
        this(null, errorListener);
    }

    public GetReportRequest() {
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
        return REQUEST_GETREPORT;
    }
}
