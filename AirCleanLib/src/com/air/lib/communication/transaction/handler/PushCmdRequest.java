package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.ResultBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.air.lib.communication.utils.LogTag;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class PushCmdRequest extends GsonRequest<ResultBean> {

    private static final String PARAM_DEVICE_IDS = "deviceIds";
    private static final String PARAM_MSG = "msg";
    private static final String PARAM_EXPIRE = "expire";

    private String mBoardUuid;
    private String mMsg;

    public PushCmdRequest(Response.Listener<ResultBean> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, APIConstant.API_PUSH_CMD, ResultBean.class, listener, errorListener);
    }

    public PushCmdRequest(Response.ErrorListener errorListener) {
        this(null, errorListener);
    }

    public PushCmdRequest() {
        this(null, null);
    }

    public void setBoardUuid(String boarduuid) {
        this.mBoardUuid = boarduuid;
    }

    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_DEVICE_IDS, mBoardUuid);
        map.put(PARAM_MSG, mMsg);
        map.put(PARAM_EXPIRE, "10");
        return map;
    }

    @Override
    protected int getRequestType() {
        return REQUEST_PUSH_CMD;
    }
}
