package com.air.phone.ui.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.model.RoomInfo;
import com.air.phone.widget.FontTextView;
import com.air.phone.widget.ProgressWheel;

import java.util.Random;

public class PurifierDetailFragment extends BaseDetailFragment {

    private RoomInfo mRoomInfo;

    private ProgressWheel mFilterProgress;
    private TextView mFilterProgressLabel;
    private FontTextView mFilterProgressValue;

    private void assignViews(View view) {
        mFilterProgress = (ProgressWheel) view.findViewById(R.id.filter_progress);
        mFilterProgressLabel = (TextView) view.findViewById(R.id.filter_progress_label);
        mFilterProgressValue = (FontTextView) view.findViewById(R.id.filter_progress_value);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.purifier_detail_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    public boolean hasRoomInfo() {
        return new Random().nextInt() % 2 == 0;
    }

    public RoomInfo getRoomInfo() {
        return new RoomInfo(new Random().nextInt() + "", new Random().nextBoolean() + "");
    }

    @Override
    public void update(DetailActivity activity) {

    }
}
