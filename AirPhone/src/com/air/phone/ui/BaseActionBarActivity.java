package com.air.phone.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.air.phone.R;

public class BaseActionBarActivity extends BaseFragmentActivity {

    private ImageView mActionBackBtn;
    private TextView mTitle;
    private View.OnClickListener mActionBarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mActionBackBtn) {
                finish();
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initActionBarViews();
    }

    private void initActionBarViews() {
        mActionBackBtn = (ImageView) findViewById(R.id.action_bar_btn_back);
        mTitle = (TextView) findViewById(R.id.action_bar_title);
        mActionBackBtn.setOnClickListener(mActionBarListener);
        mTitle.setText(getTitle());
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle.setText(titleId);
    }
}
