package com.air.board.pdu;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class FilterIdPdu extends GenericPdu<String> {

    @Override
    public int getNeedParamsLength() {
        return 4;
    }

    @Override
    public String getResult() {
        return getFilterId();
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REPORT_FILTER_ID;
    }

    public String getFilterId() {
        int[] params = getParams();
        byte[] paramBytes = new byte[params.length];
        for (int i = 0; i < params.length; i++) {
            paramBytes[i] = (byte) params[i];
        }
        long no = PduParser.unsigned4BytesToInt(paramBytes, 0);
        return no + "";
    }
}
