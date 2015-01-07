package com.air.phone.ui.init;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.data.WifiInfoList;
import com.air.lib.communication.utils.AirConstant;
import com.air.lib.communication.utils.AirUtils;
import com.air.lib.communication.utils.ConnectWifiHotspotTask;
import com.air.lib.communication.utils.ConnectWifiListener;
import com.air.lib.communication.utils.LogTag;
import com.air.phone.PhoneApplication;
import com.air.phone.R;
import com.air.phone.ui.init.InitActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ScanHomeWifiFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "ScanHomeWifiFragment";

    private static final int MSG_SCAN_BOARD = 0;
    private static final int MSG_CONNECT_BOARD = 1;
    private static final int MSG_INIT_SOCKET_CLIENT = 2;
    private static final int MSG_CONNECT_BOARD_FAILED = 4;

    private static final int MAX_FIND_BOARD_COUNT = 5;

    private Button mScan;
    private ListView mWifiList;

    private WifiManager mWifiManager;
    private ApWifiReceiver mWifiReceiver;
    private WifiApAdapter mAdapter;
    private List<ScanResult> mScanResults;
    private int mFindBoardCount = 0;

    private void assignViews(View view) {
        mScan = (Button) view.findViewById(R.id.scan);
        mScan.setOnClickListener(this);
        mWifiList = (ListView) view.findViewById(R.id.wifi_list);
        mWifiList.setOnItemClickListener(this);
        if (mScanResults == null) {
            mScanResults = new ArrayList<ScanResult>();
        }
        mAdapter = new WifiApAdapter();
        mWifiList.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWifiManager = (WifiManager) PhoneApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new ApWifiReceiver();
        startScanWifi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scan_home_wifi_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void onClick(View view) {
        if (view == mScan) {
            startScanWifi();
        }
    }

    private void findBoard(int count) {
        LogTag.log(TAG, "find board count : " + count);
        if (count >= MAX_FIND_BOARD_COUNT) {
            mScan.setText(R.string.please_confirm_board_on);
            return;
        }
        startScanWifi();
    }

    private void startScanWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        PhoneApplication.getInstance().registerReceiver(mWifiReceiver, filter);
        LogTag.log(TAG, "start scan wifi");
        boolean flag = mWifiManager.startScan();
        if (!flag) {
            startScanWifi();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ScanResult scanResult = (ScanResult) mAdapter.getItem(i);
        Activity activity = getActivity();
        if (activity instanceof InitActivity) {
            ((InitActivity) activity).launchConfirmWifiFragment(scanResult);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCAN_BOARD: {
                    findBoard(mFindBoardCount++);
                    break;
                }
                case MSG_CONNECT_BOARD: {
                    connectBoard();
                    break;
                }
                case MSG_CONNECT_BOARD_FAILED: {
                    Toast.makeText(PhoneApplication.getInstance(), R.string.toast_connect_board_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_INIT_SOCKET_CLIENT: {
                    InitActivity activity = (InitActivity) getActivity();
                    if (activity != null) {
                        activity.initPushClient();
                    }
                    break;
                }
            }
        }
    };

    public void refreshWifiList(WifiInfoList wifiInfoList) {
        mScanResults.clear();
        mScanResults.addAll(wifiInfoList.getScanResults());
        mAdapter.notifyDataSetChanged();
    }

    private void connectBoard() {
        ConnectWifiHotspotTask task = new ConnectWifiHotspotTask(PhoneApplication.getInstance());
        task.setListener(new ConnectBoardListener());
        WifiConfig wifiConfig = new WifiConfig(AirConstant.WIFI_HOTSPOT_SSID, AirConstant.WIFI_HOTSPOT_PWD, AirConstant.WIFI_HOTSPOT_TYPE);
        task.execute(wifiConfig);
    }

    class ConnectBoardListener implements ConnectWifiListener {

        @Override
        public void onNotifyWifiConnected(WifiConfig wifiConfig) {
            mHandler.sendEmptyMessageDelayed(MSG_INIT_SOCKET_CLIENT, 5000);
        }

        @Override
        public void onNotifyWifiConnectFailed(WifiConfig wifiConfig) {
            mHandler.sendEmptyMessage(MSG_CONNECT_BOARD_FAILED);
        }
    }

    class ApWifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            PhoneApplication.getInstance().unregisterReceiver(mWifiReceiver);
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            checkBoardAp(scanResults);
        }

        private void checkBoardAp(List<ScanResult> scanResults) {
            for (ScanResult scanResult : scanResults) {
                String ssid = scanResult.SSID;
                if (TextUtils.equals(ssid, AirConstant.WIFI_HOTSPOT_SSID)) {
                    mHandler.sendEmptyMessage(MSG_CONNECT_BOARD);
                    return;
                }
            }
            mHandler.sendEmptyMessageDelayed(MSG_SCAN_BOARD, 3000);
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PhoneApplication.getInstance().unregisterReceiver(mWifiReceiver);
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            updateWifiSSID(scanResults);
        }

        private void updateWifiSSID(List<ScanResult> scanResults) {
            HashMap<String, ScanResult> map = new HashMap<String, ScanResult>();
            for (ScanResult scanResult : scanResults) {
                if (TextUtils.isEmpty(scanResult.SSID)
                        || scanResult.SSID.startsWith(AirConstant.WIFI_HOTSPOT_SSID)) {
                    continue;
                }
                if (TextUtils.equals("00:00:00:00:00:00", scanResult.BSSID)) {
                    continue;
                }
                ScanResult result = map.get(scanResult.SSID);
                if (result != null && result.level < scanResult.level) {
                    continue;
                }
                map.put(scanResult.SSID, scanResult);
            }
            mScanResults.clear();
            for (ScanResult scanResult : map.values()) {
                mScanResults.add(scanResult);
            }
            Collections.sort(mScanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult scanResult, ScanResult scanResult2) {
                    if (scanResult.level < scanResult2.level) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            mAdapter.notifyDataSetChanged();
        }
    }

    class WifiApAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mScanResults == null ? 0 : mScanResults.size();
        }

        @Override
        public Object getItem(int i) {
            return mScanResults.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(PhoneApplication.getInstance()).inflate(R.layout.wifi_list_item, null);
                viewHolder.mSSID = (TextView) view.findViewById(R.id.wifi_ssid);
                viewHolder.mWifiIcon = (ImageView) view.findViewById(R.id.wifi_icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ScanResult scanResult = (ScanResult) getItem(i);
            boolean hasPassword = AirUtils.getSecurity(scanResult);
            viewHolder.mSSID.setText(scanResult.SSID);
            viewHolder.mWifiIcon.setImageResource(hasPassword ? R.drawable.ic_wifi_lock : R.drawable.ic_wifi);
            return view;
        }

        class ViewHolder {
            TextView mSSID;
            ImageView mWifiIcon;
        }
    }
}
