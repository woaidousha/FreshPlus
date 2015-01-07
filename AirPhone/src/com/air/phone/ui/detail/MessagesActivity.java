package com.air.phone.ui.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import com.air.lib.communication.utils.LogTag;
import com.air.phone.R;
import com.air.phone.db.MessageContract;
import com.air.phone.model.MessageItem;
import com.air.phone.ui.BaseActionBarActivity;
import com.air.phone.widget.stickylist.ExpandableStickyListHeadersListView;
import com.air.phone.widget.stickylist.StickyListHeadersAdapter;

import java.util.ArrayList;

public class MessagesActivity extends BaseActionBarActivity {

    private ExpandableStickyListHeadersListView mListView;
    private MessageAdapter mAdapter;

    private void assignViews() {
        mListView = (ExpandableStickyListHeadersListView) findViewById(R.id.messages_list);
        mListView.setEmptyView(findViewById(R.id.empty_view));
        mAdapter = new MessageAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);
        assignViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    class MessageAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private ArrayList<MessageItem> mWarnMessages;
        private ArrayList<MessageItem> mWorkRecordMessages;
        private ArrayList<MessageItem> mMessages;

        boolean mHasWarnMessages;
        boolean mHasWorkRecordMessages;

        private int[] mSectionIndices;
        private Integer[] mSectionTypes;

        private LayoutInflater mInflater;

        public MessageAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mMessages = new ArrayList<MessageItem>();
        }

        public void setMessages(ArrayList<MessageItem> warnMessages, ArrayList<MessageItem> workRecordMessages) {
            this.mWarnMessages = warnMessages;
            this.mWorkRecordMessages = workRecordMessages;
            notifyDataSetChanged();
        }


        private int[] getSectionIndices() {
            int[] sections = null;
            if (mHasWarnMessages && mHasWorkRecordMessages) {
                sections = new int[2];
                sections[0] = 0;
                sections[1] = mWarnMessages.size();
            } else if (mHasWarnMessages || mHasWarnMessages) {
                sections = new int[1];
                sections[0] = 0;
            } else {
                sections = new int[0];
            }
            return sections;
        }

        private Integer[] getSectionTypes() {
            Integer[] types = new Integer[mSectionIndices.length];
            if (mHasWarnMessages && mHasWorkRecordMessages) {
                types[0] = MessageContract.Message.TYPE_WARN;
                types[1] = MessageContract.Message.TYPE_WORK_RECORD;
            } else if (mHasWarnMessages || mHasWarnMessages) {
                types[0] = mHasWarnMessages ? MessageContract.Message.TYPE_WARN : MessageContract.Message.TYPE_WORK_RECORD;
            }
            return types;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            MessageHeaderHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_header_layout, null);
                holder = new MessageHeaderHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MessageHeaderHolder) convertView.getTag();
            }
            MessageItem item = (MessageItem) getItem(position);
            int type = item.getType();
            boolean isWarn = type == MessageContract.Message.TYPE_WARN;
            holder.getMessageHeaderIcon().setImageResource(
                    isWarn ? R.drawable.ic_message_header_warn : R.drawable.ic_message_header_work_record);
            holder.getMessageHeaderLabel().setText(isWarn ? R.string.message_header_warn : R.string.message_header_work_record);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return mMessages.get(position).getType();
        }

        @Override
        public int getCount() {
            int count = mMessages.size();
            LogTag.log("lyl", "count : " + count);
            return count;
        }

        @Override
        public Object getItem(int i) {
            return mMessages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mMessages.get(i).getId();
        }

        @Override
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            MessageItemHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_item_layout, null);
                holder = new MessageItemHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MessageItemHolder) convertView.getTag();
            }
            MessageItem item = (MessageItem) getItem(position);
            holder.getMessageText().setText(item.getMessage());
            holder.getMessageDate().setText(item.getDate() + "");
            return convertView;
        }

        @Override
        public Object[] getSections() {
            return mSectionTypes;
        }

        @Override
        public int getPositionForSection(int section) {
            if (mSectionIndices.length == 0) {
                return 0;
            }

            if (section >= mSectionIndices.length) {
                section = mSectionIndices.length - 1;
            } else if (section < 0) {
                section = 0;
            }
            return mSectionIndices[section];
        }

        @Override
        public int getSectionForPosition(int position) {
            for (int i = 0; i < mSectionIndices.length; i++) {
                LogTag.log("lyl", "position : " + position + ", mSectionIndices[i]:" + mSectionIndices[i]);
                if (position < mSectionIndices[i]) {
                    return i - 1;
                }
            }
            return mSectionIndices.length - 1;
        }

        @Override
        public void notifyDataSetChanged() {
            mHasWarnMessages = mWarnMessages != null && mWarnMessages.size() > 0;
            mHasWorkRecordMessages = mWorkRecordMessages != null && mWorkRecordMessages.size() > 0;
            mSectionIndices = getSectionIndices();
            mSectionTypes = getSectionTypes();
            mMessages.clear();
            if (mHasWarnMessages) {
                mMessages.addAll(mWarnMessages);
            }
            if (mHasWorkRecordMessages) {
                mMessages.addAll(mWorkRecordMessages);
            }
            if (mHasWarnMessages) {
                mWarnMessages.clear();
            }
            if (mHasWorkRecordMessages) {
                mWorkRecordMessages.clear();
            }
            super.notifyDataSetChanged();
        }
    }
}
