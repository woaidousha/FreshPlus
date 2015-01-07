package com.air.phone.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.widget.DashBoardItemLayout;
import com.air.phone.widget.FontTextView;

public class DashBoardFragment extends BaseDetailFragment {

    private FontTextView mInDoorAqiLevel;
    private FontTextView mInDoorAqiValue;
    private LinearLayout mDashBoardLayout;
    private DashBoardItemLayout mOutDoor;
    private DashBoardItemLayout mIndoorTemperature;
    private DashBoardItemLayout mIndoorHumidity;
    private TextView mDashBoardTips;

    private void assignViews(View view) {
        mInDoorAqiLevel = (FontTextView) view.findViewById(R.id.in_door_aqi_level);
        mInDoorAqiValue = (FontTextView) view.findViewById(R.id.in_door_aqi_value);
        mOutDoor = (DashBoardItemLayout) view.findViewById(R.id.out_door_aqi_value);
        mIndoorTemperature = (DashBoardItemLayout) view.findViewById(R.id.indoor_temperature);
        mIndoorHumidity = (DashBoardItemLayout) view.findViewById(R.id.indoor_humidity);
        mDashBoardTips = (TextView) view.findViewById(R.id.dash_board_tips);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dash_board_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void update(DetailActivity activity) {

    }
}
