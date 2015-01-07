package com.air.phone.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.air.phone.R;

public class StatusTextView extends FrameLayout {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PROCESSING = 1;
    public static final int STATUS_COMPLETE = 2;
    public static final int STATUS_ERROR = 3;

    private ImageView mStatusIcon;
    private TextView mMessageText;

    private String mText;
    private Drawable mStatusPendingDrawable;
    private Drawable mStatusProcessingDrawable;
    private Drawable mStatusCompleteDrawable;
    private Drawable mStatusErrorDrawable;

    private int mStatus;
    private ObjectAnimator mAnimator;

    public StatusTextView(Context context) {
        super(context);
    }

    public StatusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.status_textview, this);
        Resources resources = getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusTextView);
        Drawable pendingDrawable = a.getDrawable(R.styleable.StatusTextView_status_pending);
        if (pendingDrawable == null) {
            pendingDrawable = resources.getDrawable(R.drawable.ic_status_pending);
        }
        mStatusPendingDrawable = pendingDrawable;
        Drawable processingDrawable = a.getDrawable(R.styleable.StatusTextView_status_processing);
        if (processingDrawable == null) {
            processingDrawable = resources.getDrawable(R.drawable.ic_status_progressing);
        }
        mStatusProcessingDrawable = processingDrawable;
        Drawable completeDrawable = a.getDrawable(R.styleable.StatusTextView_status_complete);
        if (completeDrawable == null) {
            completeDrawable = resources.getDrawable(R.drawable.ic_status_complete);
        }
        mStatusCompleteDrawable = completeDrawable;
        Drawable errorDrawable = a.getDrawable(R.styleable.StatusTextView_status_error);
        if (errorDrawable == null) {
            errorDrawable = resources.getDrawable(android.R.drawable.stat_notify_error);
        }
        mStatusErrorDrawable = errorDrawable;
        mText = a.getString(R.styleable.StatusTextView_text);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mStatusIcon = (ImageView) findViewById(R.id.status_icon);
        mMessageText = (TextView) findViewById(R.id.message_text);
        mMessageText.setText(mText);
        initAnimator();
        updateStatusIcon();
    }

    public void setStatus(int status) {
        mStatus = status;
        updateStatusIcon();
    }

    private void updateStatusIcon() {
        switch (mStatus) {
            case STATUS_PENDING:
                mStatusIcon.setImageDrawable(mStatusPendingDrawable);
                break;
            case STATUS_PROCESSING:
                mStatusIcon.setImageDrawable(mStatusProcessingDrawable);
                break;
            case STATUS_COMPLETE:
                mStatusIcon.setImageDrawable(mStatusCompleteDrawable);
                break;
            case STATUS_ERROR:
                mStatusIcon.setImageDrawable(mStatusErrorDrawable);
                break;
        }
        if (mStatus == STATUS_PROCESSING) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(mStatusIcon, "Rotation", 0.0f, 360.0f);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(1500);
    }

    private void startAnimation() {
        if (mAnimator.isStarted()) {
            return;
        }
        mAnimator.start();
    }

    private void stopAnimation() {
        mAnimator.end();
    }

}
