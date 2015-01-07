package com.air.phone.ui.init;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.air.lib.communication.data.BasePushCmd;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.data.WifiInfoList;
import com.air.lib.communication.transaction.push.PushClient;
import com.air.lib.communication.transaction.push.PushMessage;
import com.air.lib.communication.transaction.push.PushMessageManager;
import com.air.lib.communication.utils.AirConstant;
import com.air.lib.communication.utils.LogTag;
import com.air.lib.communication.utils.PushSocketUtil;
import com.air.phone.PhoneApplication;
import com.air.phone.R;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;

import static com.air.lib.communication.transaction.push.PushClient.*;

public class InitActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "InitActivity";

    public static final String EXTRA_WIFI_SCAN_RESULT = "wifi_scan_result";

    private FirstInitFragment mFirstInitFragment;
    private ScanHomeWifiFragment mScanHomeWifiFragment;
    private ConfirmWifiFragment mConfirmWifiFragment;
    private ConnectBoardFragment mConnectBoardFragment;

    private RelativeLayout mActionBar;
    private ImageView mActionBarBtnBack;
    private ImageView mActionBarBtnClose;
    private TextView mActionBarTitle;
    private FrameLayout mContainer;

    private PushClient mPushClient;
    private Gson mGson = new Gson();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_WEBSOCKET_OPEN:
                    LogTag.log(TAG, "MSG_WEBSOCKET_OPEN");
                    sendPushMessage(PushSocketUtil.createReqWifiList());
                    break;
                case MSG_WEBSOCKET_CLOSE:
                    LogTag.log(TAG, "MSG_WEBSOCKET_CLOSE");
                    break;
                case MSG_WEBSOCKET_MESSAGE:
                    LogTag.log(TAG, "MSG_WEBSOCKET_MESSAGE : " + (String) msg.obj);
                    parseMessage((String) msg.obj);
                    break;
                case MSG_WEBSOCKET_ERROR:
                    LogTag.log(TAG, "MSG_WEBSOCKET_ERROR");
                    break;
            }
        }
    };

    private void assignViews() {
        mActionBar = (RelativeLayout) findViewById(R.id.action_bar);
        mActionBarBtnBack = (ImageView) findViewById(R.id.action_bar_btn_back);
        mActionBarBtnClose = (ImageView) findViewById(R.id.action_bar_btn_close);
        mActionBarBtnClose.setVisibility(View.VISIBLE);
        mActionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        mContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mActionBarBtnBack.setOnClickListener(this);
        mActionBarBtnClose.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity);
        assignViews();
        initFragment();
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount == 0) {
            super.onBackPressed();
            return;
        }
        getSupportFragmentManager().popBackStack();
    }

    private void initFragment() {
        mFirstInitFragment = new FirstInitFragment();
        mScanHomeWifiFragment = new ScanHomeWifiFragment();
        mConfirmWifiFragment = new ConfirmWifiFragment();
        mConnectBoardFragment = new ConnectBoardFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mFirstInitFragment);
        transaction.commit();
    }

    public void launchScanHomeWifiFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mScanHomeWifiFragment);
        transaction.commit();
    }

    public void launchConfirmWifiFragment(ScanResult scanResult) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_WIFI_SCAN_RESULT, scanResult);
        mConfirmWifiFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, mConfirmWifiFragment);
        transaction.commit();
    }

    public void launchConnectBoardFragment() {
        mConfirmWifiFragment.purgeTimer();
        closePushClient();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        WifiConfig config = PhoneApplication.getInstance().getWifiConfig();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConnectBoardFragment.EXTRA_WIFI_CONFIG, config);
        mConnectBoardFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, mConnectBoardFragment);
        transaction.commit();
    }

    protected void initPushClient() {
        LogTag.log(TAG, "initPushClient");
        try {
            URI uri = new URI(AirConstant.INIT_WEB_SOCKET_HOST);
            mPushClient = new PushClient(this, uri, mHandler);
            mPushClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void closePushClient() {
        if (mPushClient != null) {
            mPushClient.close();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mActionBarBtnBack) {
            onBackPressed();
        } else if (view == mActionBarBtnClose) {
            finish();
        }
    }

    private void parseMessage(String message) {
        PushMessage pushMessage = PushMessage.fromJson(message);
        switch (pushMessage.getType()) {
            case PushMessage.MsgType.TYPE_SEND: {
                String cmdMessage = pushMessage.getParams().getMessage();
                LogTag.log(TAG, "cmdMessage : " + cmdMessage);
                try {
                    BasePushCmd pushCmd = mGson.fromJson(cmdMessage, BasePushCmd.class);
                    LogTag.log(TAG, "command : " + pushCmd.toString());
                    dealPushCmd(pushCmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PushMessage respMsg = PushSocketUtil.createResponseMessage(pushMessage.getId());
                if (mPushClient != null && mPushClient.isOpen()) {
                    if (respMsg.getType() == PushMessage.MsgType.TYPE_SEND) {
                        PushMessageManager.getInstance().addSentMessage(respMsg);
                    }
                    mPushClient.send(respMsg.toJson());
                }
                break;
            }
            case PushMessage.MsgType.TYPE_RECEIVE: {
                PushMessageManager.getInstance().removeSentMessage(pushMessage.getId());
                break;
            }
        }
    }

    private void dealPushCmd(BasePushCmd pushCmd) {
        switch (pushCmd.getType()) {
            case BasePushCmd.TYPE_RES_WIFI_LIST:
                WifiInfoList wifiInfoList = mGson.fromJson(pushCmd.getMsg(), WifiInfoList.class);
                mScanHomeWifiFragment.refreshWifiList(wifiInfoList);
                break;
            case BasePushCmd.TYPE_RES_WIFI_INFO:
                launchConnectBoardFragment();
                break;
        }
    }

    public void sendPushMessage(PushMessage pushMessage) {
        if (pushMessage == null) {
            return;
        }
        if (mPushClient != null
                && mPushClient.isOpen()) {
            String str = pushMessage.toJson();
            LogTag.log(TAG, "send :" + str);
            mPushClient.send(str);
        }
    }
}
