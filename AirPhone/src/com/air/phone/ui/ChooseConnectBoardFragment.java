package com.air.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.air.phone.R;
import com.air.phone.ui.init.InitActivity;

public class ChooseConnectBoardFragment extends Fragment implements View.OnClickListener {

    private ImageView mChooseDirectBoard;
    private ImageView mChooseBindAccount;

    private void assignViews(View view) {
        mChooseDirectBoard = (ImageView) view.findViewById(R.id.choose_direct_board);
        mChooseBindAccount = (ImageView) view.findViewById(R.id.choose_bind_account);
        mChooseDirectBoard.setOnClickListener(this);
        mChooseBindAccount.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_connect_board_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
    }

    @Override
    public void onClick(View view) {
        if (view == mChooseDirectBoard) {
            launchInitActivity();
        } else if (view == mChooseBindAccount) {
            bindFamilyAccountActivity();
        }
    }

    private void launchInitActivity() {
        Intent intent = new Intent(getActivity(), InitActivity.class);
        getActivity().startActivity(intent);
    }

    private void bindFamilyAccountActivity() {
        Intent intent = new Intent(getActivity(), BindFamilyAccountActivity.class);
        getActivity().startActivity(intent);
    }
}
