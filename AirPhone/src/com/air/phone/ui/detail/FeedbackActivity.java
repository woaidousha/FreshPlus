package com.air.phone.ui.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.air.phone.R;
import com.air.phone.ui.BaseActionBarActivity;

public class FeedbackActivity extends BaseActionBarActivity implements View.OnClickListener {

    private EditText mFeedbackContent;
    private Button mSendButton;

    private void assignViews() {
        mFeedbackContent = (EditText) findViewById(R.id.feedback_content);
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        assignViews();
    }

    @Override
    public void onClick(View view) {
        if (view == mSendButton) {
            sendFeedback();
        }
    }

    private void sendFeedback() {
        String feedback = mFeedbackContent.getText().toString();
        if (TextUtils.isEmpty(feedback)) {
            mFeedbackContent.setError(getString(R.string.feedback_error_empty));
            return;
        }

        //TODO: request send feedback
    }
}