package com.air.phone.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.air.lib.communication.transaction.bean.GetReportDeviceData;
import com.air.lib.communication.transaction.bean.GetReportDeviceStatus;
import com.air.lib.communication.transaction.bean.GetReportResultBean;
import com.air.lib.communication.transaction.bean.GetReportResultData;
import com.air.lib.communication.transaction.bean.Weather;

import java.util.ArrayList;

public abstract class BaseHomeFragment extends Fragment {

    protected GetReportResultBean mResponse;
    protected GetReportDeviceData mDeviceData;
    protected GetReportDeviceStatus mDeviceStatus;
    protected Weather mWeather;
    private boolean mAssignedView = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignViews(view);
        mAssignedView = true;
        refresh();
    }

    protected void refreshData(GetReportResultBean response) {
        mResponse = response;
        GetReportResultData data = response.getData();
        if (data != null) {
            ArrayList<GetReportDeviceData> deviceDatas = data.getDataList();
            if (deviceDatas != null && deviceDatas.size() > 0) {
                mDeviceData = data.getDataList().get(0);
            }
            mDeviceStatus = data.getDeviceStatus();
            mWeather = response.getWeather();
        }
        if (mAssignedView) {
            refresh();
        }
    }

    public GetReportDeviceData getDeviceData() {
        return mDeviceData;
    }

    protected abstract void refresh();

    protected abstract void assignViews(View view);
}
