package com.air.phone.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.air.phone.R;

public class WorkModeRadioButton extends LinearLayout {

    private ImageView mStatusIcon;
    private TextView mWorkModeText;

    private String mWorkMode;
    private boolean mBig;

    private void assignViews() {
        mStatusIcon = (ImageView) findViewById(R.id.status_icon);
        mWorkModeText = (TextView) findViewById(R.id.work_mode_text);
        mWorkModeText.setText(mWorkMode);
        mStatusIcon.setBackgroundResource(mBig ? R.drawable.ic_work_mode_big_selector : R.drawable.ic_work_mode_selector);
    }

    public WorkModeRadioButton(Context context) {
        super(context);
    }

    public WorkModeRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = getResources();
        LayoutInflater.from(context).inflate(R.layout.work_mode_radio_button, this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WorkModeRadioButton);
        String labelText = a.getString(R.styleable.WorkModeRadioButton_workModeText);
        if (TextUtils.isEmpty(labelText)) {
            labelText = resources.getString(R.string.app_name);
        }
        mWorkMode = labelText;
        mBig = a.getBoolean(R.styleable.WorkModeRadioButton_big, false);
    }

    public WorkModeRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        assignViews();
    }
}
