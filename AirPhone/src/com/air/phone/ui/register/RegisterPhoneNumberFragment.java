package com.air.phone.ui.register;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.air.lib.communication.utils.AirUtils;
import com.air.phone.R;
import com.air.phone.ui.BindFamilyAccountActivity;

public class RegisterPhoneNumberFragment extends Fragment implements View.OnClickListener {

    private EditText mPhoneNumber;
    private Button mOk;

    private void assignViews(View view) {
        mPhoneNumber = (EditText) view.findViewById(R.id.phone_number);
        mOk = (Button) view.findViewById(R.id.ok);
        mOk.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_phone_number_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void onClick(View view) {
        if (mOk == view) {
            requestVerificationCode();
        }
    }

    private void requestVerificationCode() {
        String phoneNumber = mPhoneNumber.getText().toString();
        phoneNumber = "18611557617";
        boolean isPhoneNumber = AirUtils.isPhoneNumber(phoneNumber);
        if (!isPhoneNumber) {
            mPhoneNumber.setError(getString(R.string.phone_number_error));
            return;
        }
        Activity activity = getActivity();
        if (activity instanceof RegisterActivity) {
            ((RegisterActivity) activity).requestVerificationCode(phoneNumber);
        } else if (activity instanceof BindFamilyAccountActivity) {
            ((BindFamilyAccountActivity) activity).requestVerificationCode(phoneNumber);
        }
    }
}
