package com.air.update.transaction.control;

/**
 * The result for a transaction
 *
 * @author jiangrui
 */
public class TransResult {

    /**
     * A unique id for a transaction
     */
    private final long mKeyIndex;

    private final ResultObj mResult;

    private static long mCurrentTransId = 0;

    public TransResult(long keyIndex, ResultObj result) {
        mKeyIndex = keyIndex;
        mResult = result;
    }

    public long getKeyIndex() {
        return mKeyIndex;
    }

    public ResultObj getResultObj() {
        return mResult;
    }

}