package com.air.phone.jobs;

import com.air.lib.communication.jobs.BaseJob;
import com.path.android.jobqueue.Params;

public class LoginJob extends BaseJob {

    protected LoginJob() {
        super(new Params(Priority.HIGH).requireNetwork().persist().groupBy(Group.LOGIN));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
