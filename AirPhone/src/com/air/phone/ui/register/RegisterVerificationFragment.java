package com.air.phone.ui.register;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.ui.BindFamilyAccountActivity;

public class RegisterVerificationFragment extends Fragment implements View.OnClickListener {

    private TextView mSentCodeTips;
    private EditText mVerificationCode;
    private Button mOk;

    private void assignViews(View view) {
        mSentCodeTips = (TextView) view.findViewById(R.id.sent_code_tips);
        mVerificationCode = (EditText) view.findViewById(R.id.verification_code);
        mOk = (Button) view.findViewById(R.id.ok);
        mOk.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verification_code_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateTips();
        }
    }

    private void updateTips() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        String phoneNumber = "";
        if (activity instanceof RegisterActivity) {
            phoneNumber = ((RegisterActivity) activity).getPhoneNumber();
        } else if (activity instanceof BindFamilyAccountActivity) {
            phoneNumber = ((BindFamilyAccountActivity) activity).getPhoneNumber();
        }
        String tips = String.format(getString(R.string.verification_sent_tips), phoneNumber);
        mSentCodeTips.setText(tips);
    }

    @Override
    public void onClick(View view) {
        if (view == mOk) {
            verifyCode();
        }
    }

    private void verifyCode() {
        String inputCode = mVerificationCode.getText().toString();
        if (TextUtils.isEmpty(inputCode) ||
                inputCode.length() < getResources().getInteger(R.integer.verification_code_length)) {
            mVerificationCode.setError(getString(R.string.verification_length_error));
            return;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (activity instanceof RegisterActivity) {
            String verificationCode = ((RegisterActivity) activity).getVerificationCode();
            if (!TextUtils.equals(inputCode, verificationCode)) {
                mVerificationCode.setError(getString(R.string.verification_error));
                return;
            }
            ((RegisterActivity) activity).launchPassword();
        } else if (activity instanceof BindFamilyAccountActivity) {
            String verificationCode = ((RegisterActivity) activity).getVerificationCode();
            if (!TextUtils.equals(inputCode, verificationCode)) {
                mVerificationCode.setError(getString(R.string.verification_error));
                return;
            }
            ((BindFamilyAccountActivity) activity).requestBindFamilyAccount();
        }
    }
}
