package com.air.board.pdu;

public class ARMStatusPdu extends GenericPdu<Integer> {
    @Override
    public int getNeedParamsLength() {
        return 1;
    }

    @Override
    public Integer getResult() {
        return getParams()[0];
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REC_ARM_STATUS;
    }
}
