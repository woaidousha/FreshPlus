package com.air.phone.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.air.lib.communication.utils.AirUtils;
import com.air.phone.PhoneApplication;
import com.air.phone.R;

public class IndoorFragment extends BaseHomeFragment implements View.OnClickListener {

    private int mIndoorAqi;
    private int mLevel;

    private TextView mLabel;
    private TextView mInDoorAqiNumeric;
    private TextView mInDoorAqiLevel;
    private TextView mTips;
    private TextView mDetailButton;
    private TextView mSwitchButton;

    private PopupWindow mPopupWindow;

    @Override
    protected void assignViews(View view) {
        mLabel = (TextView) view.findViewById(R.id.label);
        mInDoorAqiNumeric = (TextView) view.findViewById(R.id.in_door_aqi_numeric);
        mInDoorAqiLevel = (TextView) view.findViewById(R.id.in_door_aqi_level);
        mTips = (TextView) view.findViewById(R.id.tips);
        mDetailButton = (TextView) view.findViewById(R.id.info_button);
        mSwitchButton = (TextView) view.findViewById(R.id.switch_work_mode);
        mDetailButton.setOnClickListener(this);
        mSwitchButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.in_door_fragment, null);
    }

    @Override
    public void onClick(View view) {
        if (view == mDetailButton) {
            showDetailDialog();
        } else if (view == mSwitchButton) {
            //TODO: switch work mode
        }
    }

    private void showDetailDialog() {
        if (mDeviceData != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            mPopupWindow = new PopupWindow(PhoneApplication.getInstance());
            View view = getLayoutInflater(null).inflate(R.layout.pm25_layout, null);
            ListView hlv = (ListView) view.findViewById(R.id.hlvSimpleList);
            hlv.setAdapter(new PmAdapter());
            mPopupWindow.setContentView(view);
            mPopupWindow.setWidth(width - 40);
            mPopupWindow.setHeight(height / 2);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            int[] location = new int[2];
            mDetailButton.getLocationOnScreen(location);
            mPopupWindow.showAtLocation(mDetailButton, Gravity.NO_GRAVITY, (width - mPopupWindow.getWidth()) / 2,
                    location[1] - mPopupWindow.getHeight());
        }
    }

    @Override
    protected void refresh() {
        if (mDeviceData != null) {
            mIndoorAqi = AirUtils.calculateAQI(mDeviceData.getPm25());
        }
        mLevel = AirUtils.getOutdoorAQILevel(mIndoorAqi);
        mSwitchButton.setVisibility(AirUtils.isDangerLevel(mLevel) ? View.VISIBLE : View.GONE);
        mInDoorAqiNumeric.setText(mIndoorAqi + "");
        mInDoorAqiLevel.setText(
                PhoneApplication.getInstance().getResources().getStringArray(R.array.api_level_value)[mLevel]);
        mTips.setText(PhoneApplication.getInstance().getResources().getStringArray(R.array.indoor_aqi_tips)[mLevel]);
    }

    class PmAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater(null).inflate(R.layout.pm25_item, null);
            int color = Color.parseColor(
                    PhoneApplication.getInstance().getResources().getStringArray(R.array.outdoor_aqi_color)[i]);
            boolean isSelected = i == mLevel;
            View colorLine = view.findViewById(R.id.color_line);
            TextView pmLevel = (TextView) view.findViewById(R.id.pm_level);
            TextView arrow = (TextView) view.findViewById(R.id.arrow);
            TextView pmDetail = (TextView) view.findViewById(R.id.pm_level_detail);
            colorLine.setBackgroundColor(color);
            pmLevel.setTextColor(color);
            pmLevel.setText(PhoneApplication.getInstance().getResources().getStringArray(R.array.api_level_value)[i]);
            if (isSelected) {
                arrow.setTextColor(color);
                pmDetail.setTextColor(color);
                arrow.setVisibility(View.VISIBLE);
                pmDetail.setVisibility(View.VISIBLE);
                pmDetail.setText(mIndoorAqi + "");
            } else {
                arrow.setVisibility(View.GONE);
                pmDetail.setVisibility(View.GONE);
            }

            return view;
        }
    }
}
