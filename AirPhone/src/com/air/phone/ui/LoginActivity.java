package com.air.phone.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.ui.register.RegisterActivity;
import com.air.phone.widget.HideablePasswordEditText;

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText mUserName;
    private HideablePasswordEditText mPassword;
    private Button mLogin;
    private TextView mRegisterTips;

    private void assignViews() {
        mUserName = (EditText) findViewById(R.id.user_name);
        mPassword = (HideablePasswordEditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.ok);
        mRegisterTips = (TextView) findViewById(R.id.register_tips);
        mLogin.setOnClickListener(this);
        mRegisterTips.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        assignViews();
    }

    @Override
    public void onClick(View view) {
        if (view == mLogin) {
            login();
        } else if (view == mRegisterTips) {
            register();
        }
    }

    private void login() {
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUserName.setError(getString(R.string.username_error));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.password_error));
            return;
        }
        //TODO: request server for login
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
