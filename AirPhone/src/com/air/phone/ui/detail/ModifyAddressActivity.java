package com.air.phone.ui.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.air.phone.R;
import com.air.phone.ui.BaseActionBarActivity;

public class ModifyAddressActivity extends BaseActionBarActivity implements View.OnClickListener {

    private EditText mAddress;
    private Button mSaveButton;

    private void assignViews() {
        mAddress = (EditText) findViewById(R.id.address);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_address_activity);
        assignViews();
    }

    @Override
    public void onClick(View view) {
        if (view == mSaveButton) {
            saveAddress();
        }
    }

    private void saveAddress() {
        String address = mAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            mAddress.setError(getString(R.string.address_error_empty));
            return;
        }
        //TODO: request modify address
    }
}
