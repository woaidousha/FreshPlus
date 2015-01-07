package com.air.phone.ui.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import com.air.phone.R;
import com.air.phone.ui.BaseActionBarActivity;
import com.air.phone.widget.HideablePasswordEditText;

public class ModifyPasswordActivity extends BaseActionBarActivity implements View.OnClickListener {

    private HideablePasswordEditText mOldPassword;
    private HideablePasswordEditText mNewPassword;
    private HideablePasswordEditText mRePassword;
    private Button mSaveButton;

    private void assignViews() {
        mOldPassword = (HideablePasswordEditText) findViewById(R.id.old_password);
        mNewPassword = (HideablePasswordEditText) findViewById(R.id.new_password);
        mRePassword = (HideablePasswordEditText) findViewById(R.id.re_password);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password_activity);
        assignViews();
    }

    @Override
    public void onClick(View view) {
        if (view == mSaveButton) {
            modifyPassword();
        }
    }

    private void modifyPassword() {
        String oldPassword = mOldPassword.getText().toString();
        String newPassword = mNewPassword.getText().toString();
        String repeatPassword = mRePassword.getText().toString();

        if (TextUtils.isEmpty(oldPassword)
                || TextUtils.isEmpty(newPassword)
                || TextUtils.isEmpty(repeatPassword)) {
            return;
        }

        if (!TextUtils.equals(newPassword, repeatPassword)) {
            mRePassword.setError(getString(R.string.pwd_twice_not_match));
            return;
        }

        //TODO: requeset modify password
    }
}
