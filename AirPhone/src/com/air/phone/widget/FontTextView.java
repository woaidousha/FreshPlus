package com.air.phone.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class FontTextView extends TextView {

    private static final String TAG = "FontTextView";

    public Map<String, Typeface> typefaceCache = new HashMap<String, Typeface>();

    private ValueAnimator mNumericAnimator;

    private int mCurrentNumeric;

    public FontTextView(Context context) {
        super(context);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            setTypeface(attrs, this);
        }
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            setTypeface(attrs, this);
        }
    }

    public void setTypeface(AttributeSet attrs, TextView textView) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "font.otf");
        } catch (Exception e) {
            Log.v(TAG, "font not found");
            return;
        }

        textView.setTypeface(typeface);
    }

    public void setNumeric(int num) {
        if (mCurrentNumeric == num) {
            setText(String.valueOf(num));
        } else {
            startNumericAnimator(mCurrentNumeric, num);
        }
        mCurrentNumeric = num;
    }

    private void startNumericAnimator(int start, int end) {
        if (mNumericAnimator != null) {
            mNumericAnimator.end();
        }
        mNumericAnimator = ValueAnimator.ofInt(start, end);
        mNumericAnimator.setDuration(500);
        mNumericAnimator.setInterpolator(new LinearInterpolator());
        mNumericAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setText(String.valueOf(valueAnimator.getAnimatedValue()));
            }
        });
        mNumericAnimator.start();
    }
}
