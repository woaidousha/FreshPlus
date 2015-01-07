package com.air.phone.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.air.phone.R;
import com.air.phone.ui.register.RegisterPhoneNumberFragment;
import com.air.phone.ui.register.RegisterVerificationFragment;

import java.util.Timer;
import java.util.TimerTask;

public class BindFamilyAccountActivity extends BaseFragmentActivity {

    private RegisterPhoneNumberFragment mPhoneNumberFragment;
    private RegisterVerificationFragment mVerificationFragment;

    private String mPhoneNumber;
    private String mVerificationCode;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getVerificationCode() {
        return mVerificationCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_family_account_activity);
    }

    @Override
    protected void initFragments() {
        mPhoneNumberFragment = new RegisterPhoneNumberFragment();
        mVerificationFragment = new RegisterVerificationFragment();
    }

    @Override
    protected void launchFragmentOnStart() {
        super.launchFragmentOnStart();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, mPhoneNumberFragment, RegisterPhoneNumberFragment.class.getName());
        transaction.add(R.id.fragment_container, mVerificationFragment, RegisterVerificationFragment.class.getName());
        showFragment(transaction, mPhoneNumberFragment);
        hideFragment(transaction, mVerificationFragment);
        transaction.commit();
    }

    public void requestVerificationCode(String number) {
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

    public void launchVerificationCode() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        hideFragment(transaction, mPhoneNumberFragment);
        showFragment(transaction, mVerificationFragment);
        transaction.commit();
    }

    public void requestBindFamilyAccount() {
        String message = getString(R.string.prompt_binding_family_account);
        showProgressDialog(message, false);
        //TODO: request verfication code
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dismissProgressDialog();
                onBindSuccess();
            }
        }, 2000);
    }

    private void onBindSuccess() {

    }

    private void onBindFailed() {
    }
}
