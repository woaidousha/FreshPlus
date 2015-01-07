package com.air.board.pdu;

public class AlertPdu extends GenericPdu<Integer> {
    @Override
    public int getNeedParamsLength() {
        return 1;
    }

    @Override
    public Integer getResult() {
        int[] params = getParams();
        return params[0];
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REC_ALERT;
    }

}
