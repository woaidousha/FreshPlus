package com.air.phone.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import com.air.phone.R;

public class HideablePasswordEditText extends EditText implements View.OnTouchListener {

    /**
     * Callback interface for visibility of password.
     */
    public interface OnPasswordVisibilityChangedListener {

        /**
         * Called when the password is hidden.
         */
        void onPasswordHidden();

        /**
         * Called when the password is shown.
         */
        void onPasswordShown();
    }

    private static final int LEFT   = 0;
    private static final int TOP    = 1;
    private static final int RIGHT  = 2;
    private static final int BOTTOM = 3;
    private static final int COMPOUND_DRAWABLE_COUNT = 4;

    private Drawable mHideDrawable;
    private Drawable mShowDrawable;
    private Drawable mActiveDrawable;

    /**
     * Listener used to dispatch state change events.
     */
    private OnPasswordVisibilityChangedListener mOnPasswordVisibilityChangedListener;

    @SuppressWarnings ("UnusedDeclaration")
    public HideablePasswordEditText(final Context inContext) {
        super(inContext);
        init(inContext, null);
    }

    @SuppressWarnings ("UnusedDeclaration")
    public HideablePasswordEditText(final Context inContext, final AttributeSet inAttrs) {
        super(inContext, inAttrs);
        init(inContext, inAttrs);
    }

    @SuppressWarnings ("UnusedDeclaration")
    public HideablePasswordEditText(final Context inContext, final AttributeSet inAttrs,
                                    final int inDefStyle) {

        super(inContext, inAttrs, inDefStyle);
        init(inContext, inAttrs);
    }

    @SuppressWarnings ("UnusedDeclaration")
    public void setPaint(final Paint inPaint) {
        if (inPaint == null) {
            throw new NullPointerException("Paint must not be null");
        }

        updateShowHideDrawable();
    }

    /**
     * Register a callback to be invoked when the password visibility changes.
     *
     * @param inListener The callback that will run.
     */
    public void setOnPasswordVisibilityChangedListener(OnPasswordVisibilityChangedListener inListener) {
        mOnPasswordVisibilityChangedListener = inListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(final View inView, final MotionEvent inEvent) {
        final Drawable[] drawables = getCompoundDrawables();
        if (drawables != null && drawables[RIGHT] != null) {
            final int xLeft = getWidth() - getPaddingRight() - mActiveDrawable.getIntrinsicWidth();
            boolean tappedX = inEvent.getX() > xLeft;
            if (tappedX) {
                if (inEvent.getAction() == MotionEvent.ACTION_UP) {
                    toggleVisibility();
                }
                return true;
            }
        }
        return false;
    }

    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    private void toggleVisibility() {
        int inputType = getInputType();
        final boolean passwordHidden = isPasswordHidden();
        final boolean isNumber = areBitsSet(inputType, EditorInfo.TYPE_CLASS_NUMBER);

        if (isNumber) {
            if (passwordHidden) {
                inputType = inputType & ~EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD;
            } else {
                inputType = inputType | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD;
            }

        } else {
            if (passwordHidden) {
                inputType = inputType & ~EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                        | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            } else {
                inputType = inputType & ~EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
            }
        }

        setInputType(inputType);
        updateShowHideDrawable();

        if (mOnPasswordVisibilityChangedListener != null) {
            if (passwordHidden) {
                mOnPasswordVisibilityChangedListener.onPasswordShown();
            } else {
                mOnPasswordVisibilityChangedListener.onPasswordHidden();
            }
        }
        setSelection(getText().length());
        requestFocus();
    }

    private void init(final Context inContext, final AttributeSet inAttrs) {
        setOnTouchListener(this);

        mHideDrawable = getResources().getDrawable(R.drawable.ic_password_show);
        mShowDrawable = getResources().getDrawable(R.drawable.ic_password_hidden);

        updateShowHideDrawable();
    }

    private void updateShowHideDrawable() {
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables == null || compoundDrawables.length < COMPOUND_DRAWABLE_COUNT) {
            compoundDrawables = new Drawable[COMPOUND_DRAWABLE_COUNT];
        }

        if (isPasswordHidden()) {
            mActiveDrawable = mShowDrawable;

        } else {
            mActiveDrawable = mHideDrawable;
        }

        setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[LEFT], compoundDrawables[TOP],
                mActiveDrawable, compoundDrawables[BOTTOM]);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean isPasswordHidden() {
        final int inputType = getInputType();

        boolean isTextPassword = areBitsSet(inputType, EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                && !areBitsSet(inputType, EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        boolean isNumberPassword = false;
        //noinspection ConstantConditions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            isNumberPassword = areBitsSet(inputType, EditorInfo.TYPE_CLASS_NUMBER)
                    && areBitsSet(inputType, EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
        }

        return isTextPassword || isNumberPassword;
    }

    private boolean areBitsSet(final int inInput, final int inBits) {
        return (inInput & inBits) == inBits;
    }

    private static class TextDrawable extends Drawable {

        private final String mText;

        private final Rect mRect = new Rect();
        private Paint mPaint;
        private int mWidth;
        private int mHeight;

        public TextDrawable(final String inText) {
            mText = inText;
            mPaint = generateDefaultPaint();
            updateIntrinsicDimensions();
        }

        private void updateIntrinsicDimensions() {
            mPaint.getTextBounds(mText, 0, mText.length(), mRect);
            mWidth = mRect.width();
            mHeight = mRect.height();
        }

        @Override
        public void draw(final Canvas inCanvas) {
            inCanvas.drawText(mText, 0, mHeight, mPaint);
        }

        @Override
        public void setAlpha(final int inAlpha) {
            mPaint.setAlpha(inAlpha);
        }

        @Override
        public void setColorFilter(final ColorFilter inColorFilter) {
            mPaint.setColorFilter(inColorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override public int getIntrinsicWidth() {
            return mWidth;
        }

        @Override public int getIntrinsicHeight() {
            return mHeight;
        }

        public void setPaint(Paint inPaint) {
            mPaint = inPaint;
            updateIntrinsicDimensions();
        }

        private Paint generateDefaultPaint() {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(22f);
            paint.setAntiAlias(true);
            paint.setFakeBoldText(true);
            paint.setShadowLayer(6f, 0, 0, Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.LEFT);
            return paint;
        }
    }
}
