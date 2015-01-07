package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.DeviceInfo;
import com.air.lib.communication.transaction.bean.DeviceUploadResultBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class DeviceInfoUploadRequest extends BaseUploadRequest<DeviceUploadResultBean>{

    private static final String PARAM_STATUS = "status";
    private static final String PARAM_SCENE = "scene";
    private static final String PARAM_HEALTH = "health";

    private DeviceInfo mDeviceInfo;

    public DeviceInfoUploadRequest(long dbId, Response.Listener<DeviceUploadResultBean> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.POST, APIConstant.API_UPLOAD_DEVICE_INFO, DeviceUploadResultBean.class, listener, errorListener, dbId);
    }

    public DeviceInfoUploadRequest(long dbId, Response.ErrorListener errorListener) {
        this(dbId, null, errorListener);
    }

    public DeviceInfoUploadRequest(long dbId) {
        this(dbId, null, null);
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.mDeviceInfo = deviceInfo;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_DEVICE_UUID, mDeviceInfo.getDeviceUuid());
        map.put(PARAM_STATUS, mDeviceInfo.getStatus() + "");
        map.put(PARAM_SCENE, mDeviceInfo.getScene() + "");
        map.put(PARAM_HEALTH, mDeviceInfo.getHealth() + "");
        return map;
    }

    @Override
    protected int getRequestType() {
        return REQUEST_DEVICE_INFO_UPLOAD;
    }
}
