package com.air.phone.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.air.lib.communication.data.SwitchMode;
import com.air.lib.communication.transaction.bean.DeviceInfo;
import com.air.lib.communication.transaction.control.Transaction;
import com.air.phone.PhoneApplication;
import com.air.phone.R;
import com.air.phone.ui.init.InitActivity;
import de.greenrobot.event.EventBus;

public class CleanerActivity extends Activity implements View.OnClickListener {

    private ImageView mMachine;
    private Button mInitButton;
    private TextView mStandbyButton;
    private TextView mModeFastButton;
    private TextView mModeNormalButton;
    private TextView mFilterProgress;
    private Button mMyHome;

    private void assignViews() {
        mMachine = (ImageView) findViewById(R.id.image_machine);
        mMachine.setOnClickListener(this);
        mInitButton = (Button) findViewById(R.id.init_button);
        mInitButton.setOnClickListener(this);
        mStandbyButton = (TextView) findViewById(R.id.standby_button);
        mModeFastButton = (TextView) findViewById(R.id.mode_fast_button);
        mModeNormalButton = (TextView) findViewById(R.id.mode_normal_button);
        mMyHome = (Button) findViewById(R.id.my_home);
        mStandbyButton.setOnClickListener(this);
        mModeFastButton.setOnClickListener(this);
        mModeNormalButton.setOnClickListener(this);
        mMyHome.setOnClickListener(this);
        mFilterProgress = (TextView) findViewById(R.id.filter_progress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleaner_activity);
        assignViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void updateButtons() {
        String boardUuid = PhoneApplication.getInstance().getBoardUuid();
        boolean hasConnectBoard = !TextUtils.isEmpty(boardUuid);
        mStandbyButton.setEnabled(hasConnectBoard);
        mModeFastButton.setEnabled(hasConnectBoard);
        mModeNormalButton.setEnabled(hasConnectBoard);
    }

    @Override
    public void onClick(View view) {
        if (view == mInitButton) {
            startInitActivity();
        } else if (view == mStandbyButton) {
            modifyMode(SwitchMode.CMD_MODIFY_STANDBY_MODE);
        } else if (view == mModeFastButton)  {
            modifyMode(SwitchMode.CMD_MODIFY_FAST_MODE);
        } else if (view == mModeNormalButton) {
            modifyMode(SwitchMode.CMD_MODIFY_NORMAL_MODE);
        } else if (view == mMyHome) {
            startMyHomeActivity();
        } else if (view == mMachine) {
            setBoardUuid();
        }
    }

    private void setBoardUuid() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.set_uuid_layout, null);
        final EditText uuidEditor = (EditText) view.findViewById(R.id.uuid_edit);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String boardUuid = uuidEditor.getText().toString();
                if (TextUtils.isEmpty(boardUuid)) {
                    return;
                }
                PhoneApplication.getInstance().saveBoardUuid(boardUuid);
            }
        });
        builder.create().show();
    }

    private void modifyMode(int mode) {
        updateCheckedView(mode);
        String msg = SwitchMode.createPushJson(mode, PhoneApplication.getInstance().getPhoneUuid());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Transaction.pushCmd(PhoneApplication.getInstance(), PhoneApplication.getInstance().getBoardUuid(), msg);
    }

    public void onEvent(DeviceInfo deviceInfo) {
        Toast.makeText(this, R.string.mode_switch_success, Toast.LENGTH_SHORT).show();
    }

    private void updateCheckedView(int mode) {
        if (mode == SwitchMode.CMD_MODIFY_NORMAL_MODE) {
            mStandbyButton.setSelected(false);
            mModeFastButton.setSelected(false);
            mModeNormalButton.setSelected(true);
        } else if (mode == SwitchMode.CMD_MODIFY_STANDBY_MODE) {
            mStandbyButton.setSelected(true);
            mModeFastButton.setSelected(false);
            mModeNormalButton.setSelected(false);
        } else if (mode == SwitchMode.CMD_MODIFY_FAST_MODE) {
            mStandbyButton.setSelected(false);
            mModeFastButton.setSelected(true);
            mModeNormalButton.setSelected(false);
        }
    }

    private void startInitActivity() {
        Intent intent = new Intent(this, InitActivity.class);
        startActivity(intent);
    }

    private void startMyHomeActivity() {
        Intent intent = new Intent(this, MyHomeActivity.class);
        startActivity(intent);
    }
}
