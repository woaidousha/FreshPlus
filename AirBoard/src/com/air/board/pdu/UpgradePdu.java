package com.air.board.pdu;

public class UpgradePdu extends SendPdu {

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_SEND_FIREWARE_UPGRADE;
    }
}
