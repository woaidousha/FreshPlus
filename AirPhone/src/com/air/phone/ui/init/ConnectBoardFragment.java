package com.air.phone.ui.init;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.utils.ConnectWifiHotspotTask;
import com.air.lib.communication.utils.ConnectWifiListener;
import com.air.lib.communication.utils.LogTag;
import com.air.phone.PhoneApplication;
import com.air.phone.R;
import com.androidzeitgeist.ani.discovery.Discovery;
import com.androidzeitgeist.ani.discovery.DiscoveryException;
import com.androidzeitgeist.ani.discovery.DiscoveryListener;

import java.net.InetAddress;

public class ConnectBoardFragment extends Fragment implements DiscoveryListener {

    private static final String TAG = "ConnectBoardFragment";

    public static final String EXTRA_WIFI_CONFIG = "wifi_config";

    private static final int MSG_INIT_SOCKET_CLIENT = 2;
    private static final int MSG_BOARD_RECEIVE_WIFI_INFO = 3;
    private static final int MSG_CONNECT_BOARD_FAILED = 4;
    private static final int MSG_WAIT_BOARD_CONNECT_HOME = 5;
    private static final int MSG_BOARD_CONNECTED_HOME = 6;

    private TextView mConnectBoardTips;
    private TextView mSettingUpWifi;
    private TextView mTestingWifi;
    private TextView mSettingUpInterconnect;
    private Button mDone;

    private Discovery mDiscovery;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_BOARD_FAILED: {
                    Toast.makeText(PhoneApplication.getInstance(), R.string.toast_connect_board_fail, Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_INIT_SOCKET_CLIENT: {

                    InitActivity activity = (InitActivity) getActivity();
                    if (activity != null) {
                        activity.initPushClient();
                    }
                    break;
                }
                case MSG_BOARD_RECEIVE_WIFI_INFO: {
                    mSettingUpWifi.setEnabled(true);
                    ConnectWifiHotspotTask task = new ConnectWifiHotspotTask(PhoneApplication.getInstance());
                    task.setListener(new ConnectHomeWifiListener());
                    Bundle bundle = getArguments();
                    WifiConfig wifiConfig = (WifiConfig) bundle.getSerializable(EXTRA_WIFI_CONFIG);
                    task.execute(wifiConfig);
                    break;
                }
                case MSG_WAIT_BOARD_CONNECT_HOME: {
                    mTestingWifi.setEnabled(true);
                    break;
                }
                case MSG_BOARD_CONNECTED_HOME: {
                    mSettingUpInterconnect.setEnabled(true);
                    break;
                }
            }
        }
    };

    private void assignViews(View view) {
        mConnectBoardTips = (TextView) view.findViewById(R.id.connect_board_tips);
        mConnectBoardTips = (TextView) view.findViewById(R.id.connect_board_tips);
        mSettingUpWifi = (TextView) view.findViewById(R.id.setting_up_wifi);
        mTestingWifi = (TextView) view.findViewById(R.id.testing_wifi);
        mSettingUpInterconnect = (TextView) view.findViewById(R.id.setting_up_interconnect);
        WifiConfig wifiConfig = PhoneApplication.getInstance().getWifiConfig();
        if (wifiConfig != null) {
            mConnectBoardTips.setText(String.format(getString(R.string.connect_board_tips), wifiConfig.SSID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.connect_board_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignViews(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        Message msg = mHandler.obtainMessage(MSG_BOARD_RECEIVE_WIFI_INFO);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDiscoveryStarted() {

    }

    @Override
    public void onDiscoveryStopped() {

    }

    @Override
    public void onDiscoveryError(Exception exception) {

    }

    @Override
    public void onIntentDiscovered(InetAddress address, String intent) {
        LogTag.log(TAG, "onIntentDiscovered intent:" + intent);
        mHandler.sendEmptyMessage(MSG_BOARD_CONNECTED_HOME);
//        Activity activity = getActivity();
//        if (activity instanceof InitActivity) {
//            ((InitActivity) activity).launchAddUserFragment();
//        }
    }

    class ConnectHomeWifiListener implements ConnectWifiListener {

        @Override
        public void onNotifyWifiConnected(WifiConfig wifiConfig) {
            PhoneApplication.getInstance().saveHomeWifi(wifiConfig);
            mDiscovery = new Discovery();
            mDiscovery.setDisoveryListener(ConnectBoardFragment.this);
            try {
                mDiscovery.enable();
            } catch (DiscoveryException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(MSG_WAIT_BOARD_CONNECT_HOME);
        }

        @Override
        public void onNotifyWifiConnectFailed(WifiConfig wifiConfig) {

        }
    }
}
