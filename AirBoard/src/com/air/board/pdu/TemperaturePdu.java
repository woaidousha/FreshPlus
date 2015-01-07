package com.air.board.pdu;

import com.air.lib.communication.utils.LogTag;

import java.util.Arrays;

public class TemperaturePdu extends GenericPdu<Float> {

    @Override
    public Float getResult() {
        int[] params = getParams();
        LogTag.log(TAG, "TemperaturePdu params : " + Arrays.toString(params) + ", result : " + (params[0] * 256 + params[1]) / 10);
        return (params[0] * 256 + params[1]) / 10.0f;
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REPORT_TEMPERATURE;
    }

    @Override
    public int getNeedParamsLength() {
        return 2;
    }

}

