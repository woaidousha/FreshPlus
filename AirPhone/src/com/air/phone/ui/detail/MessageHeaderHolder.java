package com.air.phone.ui.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.air.phone.R;

public class MessageHeaderHolder {
    private ImageView messageHeaderIcon;
    private TextView messageHeaderLabel;

    public MessageHeaderHolder(View view) {
        messageHeaderIcon = (ImageView) view.findViewById(R.id.message_header_icon);
        messageHeaderLabel = (TextView) view.findViewById(R.id.message_header_label);
    }

    public ImageView getMessageHeaderIcon() {
        return messageHeaderIcon;
    }

    public TextView getMessageHeaderLabel() {
        return messageHeaderLabel;
    }
}
