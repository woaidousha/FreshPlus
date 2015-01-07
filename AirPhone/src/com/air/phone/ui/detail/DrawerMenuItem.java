package com.air.phone.ui.detail;

import android.content.Context;
import android.content.Intent;
import com.air.phone.R;

public class DrawerMenuItem {
    private int iconResId;
    private int textResId;

    public DrawerMenuItem(int iconResId, int textResId) {
        this.iconResId = iconResId;
        this.textResId = textResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getTextResId() {
        return textResId;
    }

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }
}
