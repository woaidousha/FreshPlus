package com.air.board.pdu;

import com.air.board.BoardApplication;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public abstract class SendPdu extends GenericPdu<Integer> implements Serializable {

    @Override
    public byte[] buildPdu(int... params) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        setParams(params);
        outputStream.write(PduParser.FRAME_HEAD_1);
        outputStream.write(PduParser.FRAME_HEAD_2);
        outputStream.write(getDataLength());
        outputStream.write(getBuildType());
        for (int param : params) {
            outputStream.write(param);
        }
        outputStream.write(calcutateSendCheckSum());
        return outputStream.toByteArray();
    }

    private int calcutateSendCheckSum() {
        int sum = getBuildType();
        for (int dataByte : getParams()) {
            sum += dataByte;
        }
        return sum & 0xFF;
    }

    @Override
    public int getNeedParamsLength() {
        return 0;
    }

    @Override
    public Integer getResult() {
        return 0;
    }

    public void sendPdu(BoardApplication application, int... params) {
        application.startSendDataService(buildPdu(params));
    }
}
