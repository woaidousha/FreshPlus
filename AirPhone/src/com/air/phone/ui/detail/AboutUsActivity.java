package com.air.phone.ui.detail;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.ui.BaseActionBarActivity;

public class AboutUsActivity extends BaseActionBarActivity {

    private TextView mVersionLabel;

    private void assignViews() {
        mVersionLabel = (TextView) findViewById(R.id.version_label);
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            String versionName = packInfo.versionName;
            String version = String.format(getString(R.string.about_us_version), versionName);
            mVersionLabel.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);
        assignViews();
    }
}
