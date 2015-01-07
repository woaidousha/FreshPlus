package com.air.phone.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.air.phone.R;

public class HumidityFragment extends BaseHomeFragment {

    private TextView mTemperatureNumeric;
    private TextView mTemperatureTips;
    private TextView mHumidityNumeric;
    private TextView mHumidityTips;

    @Override
    protected void assignViews(View view) {
        mTemperatureNumeric = (TextView) view.findViewById(R.id.temperature_numeric);
        mTemperatureTips = (TextView) view.findViewById(R.id.temperature_tips);
        mHumidityNumeric = (TextView) view.findViewById(R.id.humidity_numeric);
        mHumidityTips = (TextView) view.findViewById(R.id.humidity_tips);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.humidity_fragment, null);
    }

    @Override
    protected void refresh() {
        if (mDeviceData == null) {
            return;
        }
        mTemperatureNumeric.setText(mDeviceData.getTemperature() + "");
        mHumidityNumeric.setText(mDeviceData.getHumidity() + "");
    }
}
