package com.air.board.pdu;

public class FilterLifePdu extends GenericPdu<Long> {
    @Override
    public int getNeedParamsLength() {
        return 2;
    }

    @Override
    public Long getResult() {
        int[] params = getParams();
        byte[] paramBytes = new byte[params.length];
        for (int i = 0; i < params.length; i++) {
            paramBytes[i] = (byte) params[i];
        }
        long no = PduParser.unsigned4BytesToInt(paramBytes, 0);
        return no;
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REPORT_FILTER_LIFE;
    }
}
