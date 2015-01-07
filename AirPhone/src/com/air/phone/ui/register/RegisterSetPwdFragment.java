package com.air.phone.ui.register;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.air.phone.R;
import com.air.phone.widget.HideablePasswordEditText;

public class RegisterSetPwdFragment extends Fragment implements View.OnClickListener {

    private HideablePasswordEditText mPassword;
    private Button mOk;

    private void assignViews(View view) {
        mPassword = (HideablePasswordEditText) view.findViewById(R.id.password);
        mOk = (Button) view.findViewById(R.id.ok);
        mOk.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_set_pwd_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void onClick(View view) {
        if (view == mOk) {
            String password = mPassword.getText().toString();
            if (TextUtils.isEmpty(password)
                    || password.length() < getResources().getInteger(R.integer.password_min_length)
                    || password.length() > getResources().getInteger(R.integer.password_max_length)) {
                mPassword.setError(getString(R.string.password_length_error));
                return;
            }
            RegisterActivity activity = (RegisterActivity) getActivity();
            activity.requestSetPwd(password);
        }
    }


}
