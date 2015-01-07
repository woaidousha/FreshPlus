package com.air.board.pdu;

public class HumidityPdu extends GenericPdu<Integer> {
    @Override
    public Integer getResult() {
        int[] params = getParams();
        return (params[0] * 256 + params[1]) / 10;
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REPORT_HUMIDITY;
    }

    @Override
    public int getNeedParamsLength() {
        return 2;
    }
}
