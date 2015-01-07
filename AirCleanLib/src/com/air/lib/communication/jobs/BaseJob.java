package com.air.lib.communication.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public abstract class BaseJob extends Job {

    public static class Priority {
        public static int LOW = 0;
        public static int MID = 500;
        public static int HIGH = 1000;
    }

    public static class Group {
        public static String LOGIN = "user-login";
    }

    protected BaseJob(Params params) {
        super(params);
    }
}
