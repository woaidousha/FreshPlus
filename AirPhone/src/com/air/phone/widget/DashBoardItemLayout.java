package com.air.phone.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.air.phone.R;

public class DashBoardItemLayout extends LinearLayout {

    private ImageView mDashBoardItemIcon;
    private FontTextView mDashBoardItemValue;
    private TextView mDashBoardItemLabel;

    private Drawable mIconDrawable;
    private String mLabelText;

    private void assignViews() {
        mDashBoardItemIcon = (ImageView) findViewById(R.id.dash_board_item_icon);
        mDashBoardItemValue = (FontTextView) findViewById(R.id.dash_board_item_value);
        mDashBoardItemLabel = (TextView) findViewById(R.id.dash_board_item_label);
        mDashBoardItemIcon.setImageDrawable(mIconDrawable);
        mDashBoardItemLabel.setText(mLabelText);
    }

    public DashBoardItemLayout(Context context) {
        super(context);
    }

    public DashBoardItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.dash_board_item_layout, this);
        Resources resources = getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DashBoardItemLayout);
        Drawable iconDrawable = a.getDrawable(R.styleable.DashBoardItemLayout_icon);
        if (iconDrawable == null) {
            iconDrawable = resources.getDrawable(R.drawable.ic_status_pending);
        }
        mIconDrawable = iconDrawable;
        String labelText = a.getString(R.styleable.DashBoardItemLayout_label);
        if (TextUtils.isEmpty(labelText)) {
            labelText = resources.getString(R.string.app_name);
        }
        mLabelText = labelText;
    }

    public DashBoardItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        assignViews();
    }

    public void setValue(int value) {
        mDashBoardItemValue.setNumeric(value);
    }
}
