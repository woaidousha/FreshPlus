package com.air.phone.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class BaseFragmentActivity extends FragmentActivity {

    protected FragmentManager mFragmentManager;
    private ProgressDialog mProgressDialog;

    private boolean mHasInitFragments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mProgressDialog = new ProgressDialog(this);
    }

    protected void initFragments() {
    }

    protected void launchFragmentOnStart() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mHasInitFragments) {
            initFragments();
            launchFragmentOnStart();
            mHasInitFragments = true;
        }
    }

    public void showFragment(FragmentTransaction ft, Fragment f) {
        if ((f != null) && f.isHidden()) ft.show(f);
    }

    public void hideFragment(FragmentTransaction ft, Fragment f) {
        if ((f != null) && !f.isHidden()) ft.hide(f);
    }

    protected void showProgressDialog(String message, boolean cancelable) {
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void showProgressDialog(int messageId, boolean cancelable) {
        showProgressDialog(getString(messageId), cancelable);
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
