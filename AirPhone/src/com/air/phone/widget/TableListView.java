package com.air.phone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.air.phone.R;

import java.util.ArrayList;
import java.util.List;

public class TableListView extends LinearLayout {

    private LayoutInflater mInflater;
    private LinearLayout mMainContainer;
    private LinearLayout mListContainer;
    private List<IListItem> mItemList;
    private ClickListener mClickListener;

    public TableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mItemList = new ArrayList<IListItem>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMainContainer = (LinearLayout) mInflater.inflate(R.layout.table_list_container, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mMainContainer, params);
        mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);
    }

    /**
     * @param title
     */
    public void addBasicItem(String title) {
        mItemList.add(new TableListItem(title));
    }

    /**
     * @param title
     * @param summary
     */
    public void addBasicItem(String title, String summary) {
        mItemList.add(new TableListItem(title, summary));
    }

    /**
     * @param title
     * @param summary
     * @param color
     */
    public void addBasicItem(String title, String summary, int color) {
        mItemList.add(new TableListItem(title, summary, color));
    }

    /**
     * @param drawable
     * @param title
     * @param summary
     */
    public void addBasicItem(int drawable, String title, String summary) {
        mItemList.add(new TableListItem(drawable, title, summary));
    }

    /**
     * @param drawable
     * @param title
     * @param summary
     */
    public void addBasicItem(int drawable, String title, String summary, int color) {
        mItemList.add(new TableListItem(drawable, title, summary, color));
    }

    /**
     * @param item
     */
    public void addBasicItem(TableListItem item) {
        mItemList.add(item);
    }

    public void commit() {

        if (mItemList.size() > 1) {
            //when the list has more than one item
            for (int i = 0; i < mItemList.size(); i++) {
                IListItem obj = mItemList.get(i);
                View tempItemView = mInflater.inflate(R.layout.table_list_item_layout, null);
                setupItem(tempItemView, obj, i);
                tempItemView.setClickable(obj.isClickable());
                mListContainer.addView(tempItemView);
            }
        } else if (mItemList.size() == 1) {
            //when the list has only one item
            View tempItemView = mInflater.inflate(R.layout.table_list_item_layout, null);
            IListItem obj = mItemList.get(0);
            setupItem(tempItemView, obj, 0);
            tempItemView.setClickable(obj.isClickable());
            mListContainer.addView(tempItemView);
        }
    }

    private void setupItem(View view, IListItem item, int index) {
        if (item instanceof TableListItem) {
            TableListItem tempItem = (TableListItem) item;
            setupBasicItem(view, tempItem, index);
        }
    }

    /**
     * @param view
     * @param item
     * @param index
     */
    private void setupBasicItem(View view, TableListItem item, int index) {
        if (item.getSubtitle() != null) {
            ((TextView) view.findViewById(R.id.text_content)).setText(item.getSubtitle());
        } else {
            ((TextView) view.findViewById(R.id.text_content)).setVisibility(View.GONE);
        }
        ((TextView) view.findViewById(R.id.address_label)).setText(item.getTitle());
        if (item.getColor() > -1) {
            ((TextView) view.findViewById(R.id.address_label)).setTextColor(item.getColor());
        }
        if (index == 0) {
            view.findViewById(R.id.divide).setVisibility(GONE);
        }
        view.setTag(index);
        if (item.isClickable()) {
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mClickListener != null) { mClickListener.onClick((Integer) view.getTag()); }
                }

            });
        }
    }

    public interface ClickListener {
        void onClick(int index);
    }

    /**
     * @return
     */
    public int getCount() {
        return mItemList.size();
    }

    /**
     *
     */
    public void clear() {
        mItemList.clear();
        mListContainer.removeAllViews();
    }

    /**
     * @param listener
     */
    public void setClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    /**
     *
     */
    public void removeClickListener() {
        this.mClickListener = null;
    }

}
