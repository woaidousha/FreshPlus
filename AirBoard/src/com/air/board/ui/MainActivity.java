package com.air.board.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.air.board.BoardApplication;
import com.air.board.R;
import com.air.board.db.BoardDatabaseHelper;
import com.air.board.db.TableDeviceData;
import com.air.board.pdu.PduParser;
import com.air.board.pdu.WorkModePdu;
import com.air.board.service.BoardService;
import com.air.board.service.SerialService;
import com.air.lib.communication.transaction.bean.AirBean;
import com.air.lib.communication.transaction.bean.DeviceDataResultBean;
import com.air.lib.communication.transaction.bean.DeviceInfo;
import com.air.lib.communication.transaction.bean.DeviceUploadResultBean;
import com.air.lib.communication.transaction.bean.GetReportResultBean;
import com.air.lib.communication.transaction.bean.RegisterResultBean;
import com.air.lib.communication.transaction.control.ErrorEvent;
import com.air.lib.communication.transaction.control.Transaction;
import com.air.lib.communication.utils.LogTag;
import com.google.gson.Gson;
import de.greenrobot.event.EventBus;

import java.sql.SQLException;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button mInit;
    private Button mRegister;
    private Button mDeviceUpload;
    private Button mDeviceUploadData;
    private Button mGetReport;
    private Button mSendBoot;
    private Button mSwitchMode;
    private Button mUpgrade;

    private int mMode = PduParser.VALUE_WORK_MODE_AUTO;

    private void assignViews() {
        mInit = (Button) findViewById(R.id.init);
        mRegister = (Button) findViewById(R.id.register);
        mDeviceUpload = (Button) findViewById(R.id.upload_device_info);
        mDeviceUploadData = (Button) findViewById(R.id.upload_device_data);
        mGetReport = (Button) findViewById(R.id.get_report);
        mSendBoot = (Button) findViewById(R.id.send_boot);
        mSwitchMode = (Button) findViewById(R.id.switch_mode);
        mUpgrade = (Button) findViewById(R.id.upgrade);
        mInit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mDeviceUpload.setOnClickListener(this);
        mDeviceUploadData.setOnClickListener(this);
        mGetReport.setOnClickListener(this);
        mSendBoot.setOnClickListener(this);
        mSwitchMode.setOnClickListener(this);
        mUpgrade.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        assignViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mInit) {
            createHotspot();
        } else if (view == mRegister) {
            Transaction.register(this, BoardApplication.getApplication().getBoardUuid());
        } else if (view  == mDeviceUpload) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceUuid(BoardApplication.getApplication().getBoardUuid());
            deviceInfo.setScene(new Random().nextInt(1));
            deviceInfo.setStatus(new Random().nextInt(2));
            deviceInfo.setHealth(new Random().nextInt(100));
            TableDeviceData tableDeviceData = new TableDeviceData();
            tableDeviceData.setTime(System.currentTimeMillis());
            Gson gson = new Gson();
            tableDeviceData.setData(gson.toJson(deviceInfo));
            tableDeviceData.setType(TableDeviceData.TYPE_DEVICE_INFO);
            tableDeviceData.setUpload(false);
            try {
                BoardDatabaseHelper.getInstance().getDeviceDataDao().create(tableDeviceData);
            } catch (SQLException e) {
                LogTag.e("", "save device info error : " + deviceInfo.toString(), e);
            }
//            Transaction.uploadDeviceInfo(this, deviceInfo, -1);
        } else if (view == mDeviceUploadData) {
            AirBean airBean = new AirBean();
            airBean.setHumidity(new Random().nextInt(80));
            airBean.setPm25(new Random().nextInt(500));
            airBean.setOrganics(new Random().nextInt(500));
            airBean.setCo2(new Random().nextInt(50));
            airBean.setTemperature(new Random().nextInt(40));
            TableDeviceData tableDeviceData = new TableDeviceData();
            tableDeviceData.setTime(System.currentTimeMillis());
            Gson gson = new Gson();
            tableDeviceData.setData(gson.toJson(airBean));
            tableDeviceData.setType(TableDeviceData.TYPE_DEVICE_DATA);
            tableDeviceData.setUpload(false);
            try {
                BoardDatabaseHelper.getInstance().getDeviceDataDao().create(tableDeviceData);
            } catch (SQLException e) {
                LogTag.e("", "save air bean error : " + airBean.toString(), e);
            }
//            Transaction.uploadDeviceData(this, BoardApplication.getApplication().getBoardUuid(), airBean, -1);
        } else if (view == mGetReport) {
//            Transaction.getReport(this, BoardApplication.getApplication().getBoardUuid());
            Transaction.getReport(this, "0123456789");
        } else if (view == mSendBoot) {
            BoardApplication.getApplication().setStatus(PduParser.VALUE_STATUS_BOOT_COMPLETE);
            SerialService.sendStatusPdu(false, true);
        } else if (view == mSwitchMode) {
            if (mMode == PduParser.VALUE_WORK_MODE_AUTO) {
                mMode = PduParser.VALUE_WORK_MODE_FAST;
            } else if (mMode == PduParser.VALUE_WORK_MODE_FAST) {
                mMode = PduParser.VALUE_WORK_MODE_STANDY;
            } else if (mMode == PduParser.VALUE_WORK_MODE_STANDY) {
                mMode = PduParser.VALUE_WORK_MODE_AUTO;
            }
            LogTag.log("lyl", "mode : " + mMode);
            WorkModePdu workModePdu = new WorkModePdu();
            BoardApplication.getApplication().startSendDataService(workModePdu.buildPdu(mMode));
        } else if (view == mUpgrade) {
            Intent intent = SerialService.createUpgradeIntent();
            BoardApplication.getApplication().startService(intent);
        }
    }

    private void createHotspot() {
        Intent intent = new Intent(this, BoardService.class);
        stopService(intent);
        intent.setAction(BoardService.ACTION_CREATE_WIFI_HOTSPOT);
        startService(intent);
    }

    public void onEvent(DeviceUploadResultBean response) {
        LogTag.log("DeviceUpload response :" + response.toString());
    }

    public void onEvent(RegisterResultBean response) {
        LogTag.log("Register response success : " + response.toString());
    }

    public void onEvent(GetReportResultBean response) {
        LogTag.log("GetReport response success : " + response.toString());
    }

    public void onEvent(DeviceDataResultBean response) {
        LogTag.log("DeviceData response success : " + response.toString());
    }

    public void onEvent(ErrorEvent errorEvent) {
        LogTag.log("DeviceData response fail : " + errorEvent.toString());
    }
}
