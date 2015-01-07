package com.air.board.pdu;

public class MotorSpeedPdu extends GenericPdu<Integer> {

    private boolean motor1;

    public MotorSpeedPdu() {
    }

    public MotorSpeedPdu(boolean motor1) {
        this.motor1 = motor1;
    }

    @Override
    public int getNeedParamsLength() {
        return 2;
    }

    @Override
    public Integer getResult() {
        int[] params = getParams();
        return params[0] * 256 + params[1];
    }

    @Override
    protected int getBuildType() {
        return motor1 ? PduParser.TYPE_REPORT_MOTOR1_SPEED : PduParser.TYPE_REPORT_MOTOR2_SPEED;
    }
}
