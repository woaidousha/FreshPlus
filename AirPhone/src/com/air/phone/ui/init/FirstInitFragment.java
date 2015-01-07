package com.air.phone.ui.init;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.air.phone.R;

public class FirstInitFragment extends Fragment implements View.OnClickListener {

    private Button mStartIntiButton;

    private void assignViews(View view) {
        mStartIntiButton = (Button) view.findViewById(R.id.start_inti_button);
        mStartIntiButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_init_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void onClick(View view) {
        if (view == mStartIntiButton) {
            Activity activity = getActivity();
            if (activity instanceof InitActivity) {
                ((InitActivity) activity).launchScanHomeWifiFragment();
            }
        }
    }
}
