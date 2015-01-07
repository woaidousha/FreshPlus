package com.air.phone.ui.detail;

import android.view.View;
import android.widget.TextView;
import com.air.phone.R;

public class MessageItemHolder {
    private TextView messageText;
    private TextView messageDate;

    public MessageItemHolder(View view) {
        messageText = (TextView) view.findViewById(R.id.message_text);
        messageDate = (TextView) view.findViewById(R.id.message_date);
    }

    public TextView getMessageText() {
        return messageText;
    }

    public TextView getMessageDate() {
        return messageDate;
    }
}
