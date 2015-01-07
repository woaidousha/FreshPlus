package com.air.phone.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import com.air.phone.R;
import com.air.phone.ui.BaseFragmentActivity;
import com.air.phone.ui.BindPurifierActivity;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends BaseFragmentActivity {

    private final static int FRAGMENT_PHONE_NUMBER = 0;
    private final static int FRAGMENT_VERIFICATION = 1;
    private final static int FRAGMENT_SET_PASSWORD = 2;

    private int mCurrentFragment;

    private FrameLayout mFragmentContainer;
    private RegisterPhoneNumberFragment mPhoneNumberFragment;
    private RegisterVerificationFragment mVerificationFragment;
    private RegisterSetPwdFragment mSetPwdFragment;

    private String mPhoneNumber;
    private String mVerificationCode;
    private String mPassword;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getVerificationCode() {
        return mVerificationCode;
    }

    private void assignViews() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        assignViews();
    }

    @Override
    protected void initFragments() {
        mPhoneNumberFragment = new RegisterPhoneNumberFragment();
        mVerificationFragment = new RegisterVerificationFragment();
        mSetPwdFragment = new RegisterSetPwdFragment();
    }

    @Override
    protected void launchFragmentOnStart() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, mPhoneNumberFragment, RegisterPhoneNumberFragment.class.getName());
        transaction.add(R.id.fragment_container, mVerificationFragment, RegisterVerificationFragment.class.getName());
        transaction.add(R.id.fragment_container, mSetPwdFragment, RegisterSetPwdFragment.class.getName());
        mCurrentFragment = FRAGMENT_PHONE_NUMBER;
        showFragment(transaction, mPhoneNumberFragment);
        hideFragment(transaction, mVerificationFragment);
        hideFragment(transaction, mSetPwdFragment);
        transaction.commit();
    }

    public void launchPhoneNumber() {
        mCurrentFragment = FRAGMENT_PHONE_NUMBER;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out);
        showFragment(transaction, mPhoneNumberFragment);
        hideFragment(transaction, mVerificationFragment);
        hideFragment(transaction, mSetPwdFragment);
        transaction.commit();
    }

    public void launchVerificationCode() {
        mCurrentFragment = FRAGMENT_VERIFICATION;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        hideFragment(transaction, mPhoneNumberFragment);
        showFragment(transaction, mVerificationFragment);
        hideFragment(transaction, mSetPwdFragment);
        transaction.commit();
    }

    public void launchPassword() {
        mCurrentFragment = FRAGMENT_SET_PASSWORD;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        hideFragment(transaction, mPhoneNumberFragment);
        hideFragment(transaction, mVerificationFragment);
        showFragment(transaction, mSetPwdFragment);
        transaction.commit();
    }

    protected void requestVerificationCode(String number) {
        mPhoneNumber = number;
        String message = String.format(getString(R.string.prompt_sending_verification_code), number);
        showProgressDialog(message, false);
        //TODO: request verfication code
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dismissProgressDialog();
                mVerificationCode = "1234";
                launchVerificationCode();
            }
        }, 2000);
    }

    private void launchBindPurifier() {
        Intent intent = new Intent(this, BindPurifierActivity.class);
        startActivity(intent);
        finish();
    }

    protected void requestSetPwd(String password) {
        mPassword = password;
        showProgressDialog(R.string.prompt_setting_password, false);
        //TODO: request setting password
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dismissProgressDialog();
                launchBindPurifier();
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        switch (mCurrentFragment) {
            case FRAGMENT_PHONE_NUMBER:
            case FRAGMENT_SET_PASSWORD:
                super.onBackPressed();
                break;
            case FRAGMENT_VERIFICATION:
                launchPhoneNumber();
                break;
        }
    }
}
