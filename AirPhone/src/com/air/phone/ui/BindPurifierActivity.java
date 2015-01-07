package com.air.phone.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.air.phone.R;
import com.air.phone.ui.register.RegisterPhoneNumberFragment;
import com.air.phone.ui.register.RegisterVerificationFragment;

public class BindPurifierActivity extends BaseFragmentActivity {

    private ChooseConnectBoardFragment mChooseConnectBoardFragment;
    private RegisterPhoneNumberFragment mPhoneNumberFragment;
    private RegisterVerificationFragment mVerificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_purifier_layout);
    }

    @Override
    protected void initFragments() {
        mChooseConnectBoardFragment = new ChooseConnectBoardFragment();
        mPhoneNumberFragment = new RegisterPhoneNumberFragment();
        mVerificationFragment = new RegisterVerificationFragment();
    }

    @Override
    protected void launchFragmentOnStart() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, mChooseConnectBoardFragment, ChooseConnectBoardFragment.class.getName());
        transaction.add(R.id.fragment_container, mPhoneNumberFragment, RegisterPhoneNumberFragment.class.getName());
        transaction.add(R.id.fragment_container, mVerificationFragment, RegisterVerificationFragment.class.getName());
        showFragment(transaction, mChooseConnectBoardFragment);
        hideFragment(transaction, mPhoneNumberFragment);
        hideFragment(transaction, mVerificationFragment);
        transaction.commit();
    }
}