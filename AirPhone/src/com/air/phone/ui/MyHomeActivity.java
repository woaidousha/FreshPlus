package com.air.phone.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.air.lib.communication.transaction.bean.GetReportDeviceData;
import com.air.lib.communication.transaction.bean.GetReportDeviceStatus;
import com.air.lib.communication.transaction.bean.GetReportResultBean;
import com.air.lib.communication.transaction.bean.GetReportResultData;
import com.air.lib.communication.transaction.bean.Weather;
import com.air.lib.communication.transaction.control.ErrorEvent;
import com.air.lib.communication.transaction.control.Transaction;
import com.air.phone.PhoneApplication;
import com.air.phone.R;
import com.air.phone.widget.FlipHorizontalTransformer;
import de.greenrobot.event.EventBus;

public class MyHomeActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "MyHomeActivity";

    public static final int STATE_LOADING = 0;
    public static final int STATE_LOAD_ERROR = 1;

    public static final int TAG_OUTDOOR_FRAGMENT = 0;
    public static final int TAG_INDOOR_FRAGMENT = 1;
    public static final int TAG_HUMIDITY_FRAGMENT = 2;

    private LinearLayout mTabLayout;
    private ImageView mOutDoor;
    private ImageView mInDoor;
    private ImageView mHumidity;
    private Button mMyPurifier;
    private FrameLayout mFragmentContainer;
    private View mLoadingLayout;
    private ViewPager mViewPager;
    private TextView mLoadingView;
    private ImageView mCircleView;
    private ObjectAnimator mAnimator;

    private IndoorFragment mIndoorFragment;
    private OutdoorFragment mOutdoorFragment;
    private HumidityFragment mHumidityFragment;
    private FragmentAdapter mAdapter;

    private GetReportResultData mGetReportResult;
    private GetReportDeviceData mDeviceData;
    private GetReportDeviceStatus mDeviceStatus;
    private Weather mWeather;

    private boolean mLoading = false;

    private View.OnClickListener mReloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startRequest();
        }
    };

    private void assignViews() {
        mTabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        mOutDoor = (ImageView) findViewById(R.id.out_door);
        mInDoor = (ImageView) findViewById(R.id.in_door);
        mHumidity = (ImageView) findViewById(R.id.humidity);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mLoadingView = (TextView) mLoadingLayout.findViewById(R.id.loading_view);
        mCircleView = (ImageView) mLoadingLayout.findViewById(R.id.circle_bg);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        mViewPager.setOnPageChangeListener(this);
        mOutDoor.setOnClickListener(this);
        mInDoor.setOnClickListener(this);
        mHumidity.setOnClickListener(this);
        mMyPurifier = (Button) findViewById(R.id.my_purifier);
        mMyPurifier.setOnClickListener(this);
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_home_activity);
        assignViews();
        initFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRequest();
    }

    private void initFragments() {
        mIndoorFragment = new IndoorFragment();
        mOutdoorFragment = new OutdoorFragment();
        mHumidityFragment = new HumidityFragment();
    }

    private void startRequest() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        mInDoor.setEnabled(false);
        mOutDoor.setEnabled(false);
        mHumidity.setEnabled(false);
        EventBus.getDefault().register(this);
        setLoadState(STATE_LOADING, null);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        Transaction.getReport(this, PhoneApplication.getInstance().getBoardUuid());
    }

    public void onEvent(GetReportResultBean response) {
        mLoading = false;
        GetReportResultData data = response.getData();
        if (data != null) {
            onRequestSuccess(response);
        } else {
            setLoadState(STATE_LOAD_ERROR, mReloadListener);
        }
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ErrorEvent errorEvent) {
        mLoading = false;
        EventBus.getDefault().unregister(this);
        setLoadState(STATE_LOAD_ERROR, mReloadListener);
    }

    public void onRequestSuccess(GetReportResultBean response) {
        mLoadingLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mIndoorFragment.refreshData(response);
        mOutdoorFragment.refreshData(response);
        mHumidityFragment.refreshData(response);
        mInDoor.setEnabled(true);
        mOutDoor.setEnabled(true);
        mHumidity.setEnabled(true);
        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        updateCheckedView(R.id.out_door);
    }

    protected GetReportDeviceData getReportDeviceData() {
        return mDeviceData;
    }

    protected GetReportDeviceStatus getReportDeviceStatus() {
        return mDeviceStatus;
    }

    @Override
    public void onClick(View view) {
        if (view == mMyPurifier) {
            finish();
        } else if (view == mInDoor) {
            updateCheckedView(R.id.in_door);
        } else if (view == mOutDoor) {
            updateCheckedView(R.id.out_door);
        } else if (view == mHumidity) {
            updateCheckedView(R.id.humidity);
        }
    }

    private void updateCheckedView(int checkedId) {
        mLoadingLayout.setVisibility(View.GONE);
        int position = -1;
        if (checkedId == R.id.out_door) {
            position = TAG_OUTDOOR_FRAGMENT;
            mViewPager.setCurrentItem(TAG_OUTDOOR_FRAGMENT);
        } else if (checkedId == R.id.in_door) {
            position = TAG_INDOOR_FRAGMENT;
            mViewPager.setCurrentItem(TAG_INDOOR_FRAGMENT);
        } else if (checkedId == R.id.humidity) {
            position = TAG_HUMIDITY_FRAGMENT;
        }
        updateTabView(position);
        mViewPager.setCurrentItem(position);
    }

    private void updateTabView(int posistion) {
        if (posistion == TAG_OUTDOOR_FRAGMENT) {
            mInDoor.setSelected(false);
            mHumidity.setSelected(false);
            mOutDoor.setSelected(true);
        } else if (posistion == TAG_INDOOR_FRAGMENT) {
            mInDoor.setSelected(true);
            mHumidity.setSelected(false);
            mOutDoor.setSelected(false);
        } else if (posistion == TAG_HUMIDITY_FRAGMENT) {
            mInDoor.setSelected(false);
            mHumidity.setSelected(true);
            mOutDoor.setSelected(false);
        }
    }

    private void setLoadState(int state, View.OnClickListener listener) {
        ensureAnimation();
        if (state == STATE_LOADING) {
            mLoadingView.setText(R.string.loading_data);
            mLoadingView.setOnClickListener(null);
            mAnimator.start();
        } else if (state == STATE_LOAD_ERROR) {
            mLoadingView.setText(R.string.loading_data_error);
            mLoadingView.setOnClickListener(listener);
            mAnimator.end();
        }
    }

    private void ensureAnimation() {
        if (mAnimator != null) {
            return;
        }
        mAnimator = ObjectAnimator.ofFloat(mCircleView, "Rotation", 0.0f, 360.0f);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(3000);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        updateTabView(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if (i == TAG_OUTDOOR_FRAGMENT) {
                fragment = mOutdoorFragment;
            } else if (i == TAG_INDOOR_FRAGMENT) {
                fragment = mIndoorFragment;
            } else if (i == TAG_HUMIDITY_FRAGMENT) {
                fragment = mHumidityFragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}