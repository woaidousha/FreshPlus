package com.air.update.transaction.control;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Main service for handling transactions
 */
public class Transaction {
    public static final int KEY_INDEX_CHECK_VISION = 1;
    public static final boolean ENABLE_LOG = true;

    public static Queue<TransInfo> mTransQueue = new LinkedList<TransInfo>();

    private static boolean hasSameTransInfo(TransInfo trans) {
        // if (trans.equals(mNetThread.getCurrentTransInfo())) {
        // Log.w(LOGTAG, "hasSameTransInfo, same trans info!");
        // return true;
        // }
        // for (TransInfo ti : mTransQueue) {
        // if (ti.equals(trans)) {
        // Log.w(LOGTAG, "enqueueTransInfo, got same trans info, ignore!");
        // return true;
        // }
        // }
        return false;
    }

    private static void enqueueTransInfo(TransInfo trans) {
        if (trans == null) {
            throw new IllegalArgumentException();
        }

        synchronized (mTransQueue) {
            if (hasSameTransInfo(trans))
                return;

            mTransQueue.offer(trans);
            mTransQueue.notifyAll();
        }
    }

}
