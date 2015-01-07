package com.air.phone.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.ui.BaseActionBarActivity;
import com.air.phone.widget.TableListView;

public class AccountInfoActivity extends BaseActionBarActivity implements View.OnClickListener {

    private TableListView mAccountsList;
    private RelativeLayout mModifyAddressLayout;
    private TextView mAddressText;
    private RelativeLayout mModifyPasswordLayout;

    private void assignViews() {
        mAccountsList = (TableListView) findViewById(R.id.accounts_list);
        mModifyAddressLayout = (RelativeLayout) findViewById(R.id.modify_address_layout);
        mAddressText = (TextView) findViewById(R.id.address_text);
        mModifyPasswordLayout = (RelativeLayout) findViewById(R.id.modify_password_layout);
        mModifyAddressLayout.setOnClickListener(this);
        mModifyPasswordLayout.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_activity);
        assignViews();
    }

    @Override
    public void onClick(View view) {
        if (view == mModifyAddressLayout) {
            launchModifyAddressActivity();
        } else if (view == mModifyPasswordLayout) {
            launchModifyPasswordActivity();
        }
    }

    private void launchModifyAddressActivity() {
        Intent intent = new Intent(this, ModifyAddressActivity.class);
        startActivity(intent);
    }

    private void launchModifyPasswordActivity() {
        Intent intent = new Intent(this, ModifyPasswordActivity.class);
        startActivity(intent);
    }
}
