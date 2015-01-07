package com.air.board.pdu;

public class DownMotorSpeedPdu extends SendPdu {

    private int upperLimit;
    private int lowerLimit;

    public DownMotorSpeedPdu(int lowerLimit, int upperLimit) {
        if (upperLimit < 0 || lowerLimit < 0) {
            throw new IllegalArgumentException("limit value need 0-100");
        }
        if (upperLimit > 100 || lowerLimit > 100) {
            throw new IllegalArgumentException("limit value need 0-100");
        }
        if (upperLimit < lowerLimit) {
            throw new IllegalArgumentException("upperLimit < lowerLimit");
        }
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
    }

    @Override
    public byte[] buildPdu(int... params) {
        return super.buildPdu(lowerLimit, upperLimit);
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_SEND_MOTOR_SPEED;
    }
}
