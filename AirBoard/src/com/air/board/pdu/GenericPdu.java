package com.air.board.pdu;

import java.util.Arrays;

public abstract class GenericPdu<T> {

    public static final String TAG = "GenericPdu";

    public static final int HEADER_LENGTH = 2;
    public static final int DATA_TYPE_LENGTH = 1;
    public static final int DATA_LENGTH_LENGTH = 1;
    public static final int CHECK_SUM_LENGTH = 2;

    private byte[] mPackageBytes;
    private int mType;
    private int[] mParams;
    private int mCheckSum;
    private int mDataLength;

    public abstract int getNeedParamsLength();
    public abstract T getResult();

    public byte[] buildPdu(int... params) {
        return null;
    }

    public void setPackageBytes(byte[] packageBytes) {
        this.mPackageBytes = packageBytes;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getType() {
        return mType;
    }

    public int getDataLength() {
        return mParams.length + DATA_TYPE_LENGTH;
    }

    public void setDataLengthLength(int dataLengthLength) {
        mDataLength = dataLengthLength;
    }

    public int[] getParams() {
        if (mParams == null) {
            mParams = new int[0];
        }
        return mParams;
    }

    public void setParams(int[] params) {
        this.mParams = params;
    }

    public int getCheckSum() {
        return mCheckSum;
    }

    public void setCheckSum(int checkSum) {
        this.mCheckSum = checkSum;
    }

    public int calculateCheckSum() {
        int sum = mType;
        for (int dataByte : mParams) {
            sum += dataByte;
        }
        return sum & 0xFF;
    }

    protected abstract int getBuildType();
    @Override
    public String toString() {
        return "GenericPdu{" +
                "mPackageBytes=" + Arrays.toString(mPackageBytes) +
                ", mType=" + mType +
                ", mParams=" + Arrays.toString(mParams) +
                ", mCheckSum=" + mCheckSum +
                '}';
    }
}
