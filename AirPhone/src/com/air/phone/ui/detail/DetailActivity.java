package com.air.phone.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.air.phone.R;
import com.air.phone.model.RoomInfo;
import com.air.phone.ui.BaseFragmentActivity;
import com.air.phone.widget.CirclePageIndicator;
import com.air.phone.widget.FontTextView;

import java.util.ArrayList;

public class DetailActivity extends BaseFragmentActivity implements DrawerLayout.DrawerListener, View.OnClickListener, AdapterView.OnItemClickListener,
        ViewPager.OnPageChangeListener {

    private DrawerLayout mDrawerLayout;
    private View mDrawerContent;
    private ListView mMenuList;
    private View mMenuDrawer;
    private ImageView mMenuSwitcher;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private View mSetRoomLayout;
    private LinearLayout mSetRoomInfoLayout;
    private TextView mSetRoomTips;
    private LinearLayout mRoomInfoLayout;
    private FontTextView mRoomName;
    private FontTextView mRoomArea;

    private MenuAdapter mMenuAdapter;

    private ArrayList<BaseDetailFragment> mFragments;
    private DetailFragmentAdapter mAdapter;

    private void assignViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContent = findViewById(R.id.drawer_content);
        mMenuDrawer = findViewById(R.id.menu_drawer);
        mMenuList = (ListView) findViewById(R.id.menu_list);
        mMenuSwitcher = (ImageView) findViewById(R.id.menu_switcher);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        mSetRoomLayout = findViewById(R.id.set_room_info_layout);
        mSetRoomTips = (TextView) findViewById(R.id.set_room_tips);
        mRoomInfoLayout = (LinearLayout) findViewById(R.id.room_info_layout);
        mRoomName = (FontTextView) findViewById(R.id.room_name);
        mRoomArea = (FontTextView) findViewById(R.id.room_area);
        mViewPager.setOnPageChangeListener(mIndicator);
        mDrawerLayout.setDrawerListener(this);
        mMenuSwitcher.setOnClickListener(this);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.app_name));
        mMenuAdapter = new MenuAdapter();
        mMenuList.setAdapter(mMenuAdapter);
        mMenuList.setOnItemClickListener(this);
        mIndicator.setOnPageChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        assignViews();
    }

    @Override
    protected void initFragments() {
        mFragments = new ArrayList<BaseDetailFragment>();
        mFragments.add(new DashBoardFragment());
        mFragments.add(new PurifierDetailFragment());
        mFragments.add(new PurifierDetailFragment());
        mAdapter = new DetailFragmentAdapter(mFragmentManager);
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onDrawerSlide(View view, float v) {
        mDrawerContent.setX(view.getX() + view.getWidth());
        mMenuSwitcher.setRotation(90 * v);
    }

    @Override
    public void onDrawerOpened(View view) {

    }

    @Override
    public void onDrawerClosed(View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    public void onBackPressed() {
        if (ensureMenuClose()) {
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mMenuSwitcher) {
            toggleMenu();
        }
    }

    private boolean isMenuOpen() {
        return mDrawerLayout.isDrawerOpen(mMenuDrawer);
    }

    private boolean ensureMenuClose() {
        if (isMenuOpen()) {
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }

    private void toggleMenu() {
        if (isMenuOpen()) {
            mDrawerLayout.closeDrawers();
        } else {
            mDrawerLayout.openDrawer(mMenuDrawer);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DrawerMenuItem menuItem = (DrawerMenuItem) adapterView.getItemAtPosition(i);
        onMenuItemClick(menuItem);
    }

    public void onMenuItemClick(DrawerMenuItem menuItem) {
        Intent intent = null;
        switch (menuItem.getIconResId()) {
            case R.drawable.ic_menu_messages:
                intent = new Intent(this, MessagesActivity.class);
                break;
            case R.drawable.ic_menu_accounts:
                intent = new Intent(this, AccountInfoActivity.class);
                break;
            case R.drawable.ic_menu_qa:
                intent = new Intent(this, QaActivity.class);
                break;
            case R.drawable.ic_menu_feedback:
                intent = new Intent(this, FeedbackActivity.class);
                break;
            case R.drawable.ic_menu_about_us:
                intent = new Intent(this, AboutUsActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ensureMenuClose();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        Fragment fragment = mAdapter.getItem(i);
        boolean showSwitcher = fragment instanceof DashBoardFragment;
        toggleShowMenuSwitcher(showSwitcher, fragment);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void toggleShowMenuSwitcher(boolean showSwitcher, Fragment fragment) {
        mSetRoomLayout.setVisibility(showSwitcher ? View.GONE : View.VISIBLE);
        mMenuSwitcher.setVisibility(showSwitcher ? View.VISIBLE : View.GONE);
        if (!showSwitcher) {
            boolean hasRoomInfo = ((PurifierDetailFragment) fragment).hasRoomInfo();
            mSetRoomTips.setVisibility(hasRoomInfo ? View.GONE : View.VISIBLE);
            mRoomInfoLayout.setVisibility(hasRoomInfo ? View.VISIBLE : View.GONE);
            if (hasRoomInfo) {
                RoomInfo roomInfo = ((PurifierDetailFragment) fragment).getRoomInfo();
                if (roomInfo != null) {
                    mRoomName.setText(roomInfo.getRoomName());
                    mRoomArea.setText(roomInfo.getRoomArea());
                }
            }
        }
    }

    class MenuAdapter extends BaseAdapter {

        private DrawerMenuItem[] mDrawerMenuItems = {
                new DrawerMenuItem(R.drawable.ic_menu_messages, R.string.menu_item_messages),
                new DrawerMenuItem(R.drawable.ic_menu_accounts, R.string.menu_item_accounts),
                new DrawerMenuItem(R.drawable.ic_menu_qa, R.string.menu_item_qa),
                new DrawerMenuItem(R.drawable.ic_menu_feedback, R.string.menu_item_feedback),
                new DrawerMenuItem(R.drawable.ic_menu_about_us, R.string.menu_item_about_us)
        };

        @Override
        public int getCount() {
            return mDrawerMenuItems.length;
        }

        @Override
        public Object getItem(int i) {
            return mDrawerMenuItems[i];
        }

        @Override
        public long getItemId(int i) {
            return mDrawerMenuItems[i].getIconResId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DetailMenuItemLayoutHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.detail_menu_item_layout, null);
                holder = new DetailMenuItemLayoutHolder(view);
                view.setTag(holder);
            } else {
                holder = (DetailMenuItemLayoutHolder) view.getTag();
            }
            DrawerMenuItem item = (DrawerMenuItem) getItem(i);
            holder.getMenuItemIcon().setImageResource(item.getIconResId());
            holder.getMenuItemText().setText(item.getTextResId());
            return view;
        }
    }

    class DetailFragmentAdapter extends FragmentPagerAdapter {

        public DetailFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
