package com.air.phone.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.air.lib.communication.utils.AirUtils;
import com.air.phone.PhoneApplication;
import com.air.phone.R;

public class OutdoorFragment extends BaseHomeFragment {

    private int mOutDoorAqi;

    private TextView mLabel;
    private TextView mOutDoorAqiNumeric;
    private TextView mOutDoorAqiLevel;
    private TextView mTips;

    @Override
    protected void assignViews(View view) {
        mLabel = (TextView) view.findViewById(R.id.label);
        mOutDoorAqiNumeric = (TextView) view.findViewById(R.id.out_door_aqi_numeric);
        mOutDoorAqiLevel = (TextView) view.findViewById(R.id.out_door_aqi_level);
        mTips = (TextView) view.findViewById(R.id.tips);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.out_door_fragment, null);
    }

    @Override
    protected void refresh() {
        if (mWeather != null) {
            mOutDoorAqi = mWeather.getAqi();
        }
        int level = AirUtils.getOutdoorAQILevel(mOutDoorAqi);
        mOutDoorAqiNumeric.setText(mOutDoorAqi + "");
        mOutDoorAqiLevel.setText(PhoneApplication.getInstance().getResources().getStringArray(R.array.api_level_value)[level]);
        mTips.setText(PhoneApplication.getInstance().getResources().getStringArray(R.array.outdoor_aqi_tips)[level]);
    }
}
