package com.air.phone.db;

import android.provider.BaseColumns;

public class MessageContract {

    public class Message implements BaseColumns {

        public static final int TYPE_WARN = 0;
        public static final int TYPE_WORK_RECORD = 1;

        public static final String MESSAGE = "message";
        public static final String DATE = "date";
        public static final String TYPE = "type";

    }

}
