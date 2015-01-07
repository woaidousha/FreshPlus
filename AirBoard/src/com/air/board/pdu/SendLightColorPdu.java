package com.air.board.pdu;

public class SendLightColorPdu extends SendPdu {

    private int red;
    private int green;
    private int blue;

    public SendLightColorPdu(int red, int green, int blue) {
        if (red > 255 || green > 255 || blue > 255) {
            throw new IllegalArgumentException("color value is between 0-255");
        }
        if (red < 0 || green < 0 || blue < 0) {
            throw new IllegalArgumentException("color value is between 0-255");
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_SEND_LIGHT_COLOR;
    }

    @Override
    public byte[] buildPdu(int... params) {
        return super.buildPdu(red, green, blue);
    }
}
