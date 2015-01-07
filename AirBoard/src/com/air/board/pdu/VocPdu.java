package com.air.board.pdu;

public class VocPdu extends GenericPdu<Integer> {
    @Override
    public int getNeedParamsLength() {
        return 2;
    }

    @Override
    public Integer getResult() {
        int params[] = getParams();
        return params[0] * 256 + params[1];
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REPORT_VOC;
    }
}
