package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.AddUserResultBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.air.lib.communication.utils.LogTag;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class AddUserRequest extends GsonRequest<AddUserResultBean> {

    private static final String PARAM_USERNAME = "loginName";
    private static final String PARAM_PASSWORD = "pwd";
    private static final String PARAM_BOARD_UUID = "deviceUuid";

    private String mUsername;
    private String mPassword;
    private String mBoardUuid;

    public AddUserRequest(Response.Listener<AddUserResultBean> listener, Response.ErrorListener errorListener) {
        super(Method.POST, APIConstant.API_ADDUSER, AddUserResultBean.class, listener, errorListener);
    }

    public AddUserRequest(Response.ErrorListener errorListener) {
        this(null, errorListener);
    }

    public AddUserRequest() {
        this(null, null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_USERNAME, mUsername);
        map.put(PARAM_PASSWORD, mPassword);
        map.put(PARAM_BOARD_UUID, mBoardUuid);
        return map;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public void setBoardUuid(String boarduuid) {
        this.mBoardUuid = boarduuid;
    }

    @Override
    protected int getRequestType() {
        return REQUEST_ADD_USER;
    }
}
