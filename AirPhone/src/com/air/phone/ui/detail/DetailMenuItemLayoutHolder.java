package com.air.phone.ui.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.air.phone.R;

public class DetailMenuItemLayoutHolder {
    private TextView menuItemText;
    private ImageView menuItemIcon;
    private TextView menuItemCounter;

    public DetailMenuItemLayoutHolder(View view) {
        menuItemText = (TextView) view.findViewById(R.id.menu_item_text);
        menuItemIcon = (ImageView) view.findViewById(R.id.menu_item_icon);
        menuItemCounter = (TextView) view.findViewById(R.id.menu_item_counter);
    }

    public TextView getMenuItemText() {
        return menuItemText;
    }

    public ImageView getMenuItemIcon() {
        return menuItemIcon;
    }

    public TextView getMenuItemCounter() {
        return menuItemCounter;
    }
}
