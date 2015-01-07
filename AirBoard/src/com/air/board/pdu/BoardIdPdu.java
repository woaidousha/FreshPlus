package com.air.board.pdu;

public class BoardIdPdu extends GenericPdu<String> {
    @Override
    public int getNeedParamsLength() {
        return 12;
    }

    @Override
    public String getResult() {
        int[] params = getParams();
        String uuid = "";
        for (int param : params) {
            if (param < 10) {
                uuid += "0";
            }
            uuid += param;
        }
        return uuid;
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_REPORT_BOARD_ID;
    }
}
