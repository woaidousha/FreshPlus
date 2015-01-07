package com.air.lib.communication.transaction.handler;

import com.air.lib.communication.transaction.bean.AirBean;
import com.air.lib.communication.transaction.bean.DeviceDataResultBean;
import com.air.lib.communication.transaction.control.APIConstant;
import com.air.lib.communication.utils.LogTag;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class DeviceDataUploadRequest extends BaseUploadRequest<DeviceDataResultBean> {

    private static final String PARAM_HUMIDITY = "humidity";
    private static final String PARAM_PM_25 = "pm25";
    private static final String PARAM_ORGANICS = "organics";
    private static final String PARAM_CO2 = "co2";
    private static final String PARAM_TEMPERATURE = "temperature";

    private AirBean mAirBean;

    public DeviceDataUploadRequest(long dbId, Response.Listener<DeviceDataResultBean> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.POST, APIConstant.API_UPLOAD_DEVICE_DATA, DeviceDataResultBean.class, listener, errorListener, dbId);
    }

    public DeviceDataUploadRequest(long dbId, Response.ErrorListener errorListener) {
        this(dbId, null, errorListener);
    }

    public DeviceDataUploadRequest(long dbId) {
        this(dbId, null, null);
    }

    public void setAirBean(AirBean airBean) {
        this.mAirBean = airBean;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        LogTag.log(DeviceDataUploadRequest.class.getName(), "airbean : " + mAirBean.toString());
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_DEVICE_UUID, mDeviceUuid);
        map.put(PARAM_HUMIDITY, mAirBean.getHumidity() + "");
        map.put(PARAM_PM_25, mAirBean.getPm25() + "");
        map.put(PARAM_ORGANICS, mAirBean.getOrganics() + "");
        map.put(PARAM_CO2, mAirBean.getCo2() + "");
        map.put(PARAM_TEMPERATURE, mAirBean.getTemperature() + "");
        return map;
    }

    @Override
    protected int getRequestType() {
        return REQUEST_DEVICE_DATA_UPLOAD;
    }
}
