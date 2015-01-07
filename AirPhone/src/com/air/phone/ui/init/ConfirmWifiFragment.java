package com.air.phone.ui.init;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.utils.PushSocketUtil;
import com.air.lib.communication.utils.WifiAdmin;
import com.air.phone.PhoneApplication;
import com.air.phone.R;

import java.util.Timer;
import java.util.TimerTask;

public class ConfirmWifiFragment extends Fragment implements View.OnClickListener {

    private static final int TIME_OUT_DELAY = 5000;

    private ScanResult mScanResult;

    private TextView mConfirmWifiLabel;
    private EditText mPassword;
    private Button mOkButton;
    private Timer mTimer;
    private TimerTask mTimeoutTask;

    private void assignViews(View view) {
        mConfirmWifiLabel = (TextView) view.findViewById(R.id.confirm_wifi_label);
        mPassword = (EditText) view.findViewById(R.id.password);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.confirm_wifi_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScanResult = getArguments().getParcelable(InitActivity.EXTRA_WIFI_SCAN_RESULT);
        updateLabel();
    }

    private void updateLabel() {
        if (mScanResult != null) {
            String label = String.format(getString(R.string.confirm_wifi_label), mScanResult.SSID);
            mConfirmWifiLabel.setText(label);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mOkButton) {
            sendWifiConfig();
        }
    }

    private void sendWifiConfig() {
        String pwd = mPassword.getText().toString();
        int security = WifiConfig.getSecurity(mScanResult.capabilities);
        if (security != WifiAdmin.TYPE_NO_PASSWD && TextUtils.isEmpty(pwd)) {
            return;
        }
        updateUi(true);
        WifiConfig wifiConfig = new WifiConfig(mScanResult.SSID, pwd, mScanResult.capabilities);
        InitActivity activity = (InitActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.sendPushMessage(PushSocketUtil.createWifiConfig(wifiConfig));
        PhoneApplication.getInstance().setWifiConfig(wifiConfig);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.purge();
        mTimeoutTask = new TimerTask() {
            @Override
            public void run() {
                Activity uiActivity = getActivity();
                if (uiActivity != null) {
                    uiActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPassword.setError(getString(R.string.error_send_wifi_info));
                        }
                    });
                }
            }
        };
        mTimer.schedule(mTimeoutTask, TIME_OUT_DELAY);
    }

    public void purgeTimer() {
        if (mTimer != null) {
            mTimer.purge();
        }
    }

    private void updateUi(boolean connecting) {
        mPassword.setEnabled(!connecting);
        mOkButton.setEnabled(!connecting);
    }

}
