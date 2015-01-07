package com.air.board.pdu;

public class StatusPdu extends SendPdu {

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_SEND_STATUS_CHANGE;
    }
}
