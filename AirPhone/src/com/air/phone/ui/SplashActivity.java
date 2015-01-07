package com.air.phone.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.air.phone.PhoneApplication;
import com.air.phone.R;
import com.air.phone.ui.init.InitActivity;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if (!PhoneApplication.getInstance().hasLogined()) {
                    intent.setClass(SplashActivity.this, LoginActivity.class);
                } else {
                    if (TextUtils.isEmpty(PhoneApplication.getInstance().getBoardUuid())) {
                        intent.setClass(SplashActivity.this, InitActivity.class);
                    } else {
                        intent.setClass(SplashActivity.this, CleanerActivity.class);
                    }
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
