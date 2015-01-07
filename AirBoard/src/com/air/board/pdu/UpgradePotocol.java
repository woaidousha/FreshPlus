package com.air.board.pdu;

import com.air.lib.communication.utils.LogTag;

import java.io.ByteArrayOutputStream;

public class UpgradePotocol {

    public static final String HEADER_STRING = "FF00";

    public static final int FRAME_HEAD_1 = 0xFF;
    public static final int FRAME_HEAD_2 = 0x00;

    public static final int HEADER_LENGTH = 2;
    public static final int DATA_LENGTH_LENGTH = 1;

    public static final int TYPE_D_CONNECT_BOARD = 0x00;
    public static final int TYPE_D_READY_TO_TRANSFER = 0x01;
    public static final int TYPE_D_TRANSFER_DATA = 0x02;
    public static final int TYPE_D_TRANSFER_FINISH = 0x03;
    public static final int TYPE_D_RUN_FIRMWARE = 0x04;
    public static final int TYPE_D_REBOOT_BOARD = 0x05;
    public static final int TYPE_U_CONNECT_SUCCESS = 0x80;
    public static final int TYPE_U_READY_TO_TRANSFER = 0x81;
    public static final int TYPE_U_DATA_RESPONSE = 0x82;
    public static final int TYPE_U_CHECK_FINISH = 0x83;

    public static final int VALUE_READY_RESP_FAILED = 0;
    public static final int VALUE_READY_RESP_SUCCESS = 1;

    public static byte[] connectBoard() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FRAME_HEAD_1);
        outputStream.write(FRAME_HEAD_2);
        outputStream.write(2);
        outputStream.write(TYPE_D_CONNECT_BOARD);
        outputStream.write(0);
        outputStream.write(calculateCheckSum(0));
        return outputStream.toByteArray();
    }

    public static byte[] readyToTransfer(int fileLength) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FRAME_HEAD_1);
        outputStream.write(FRAME_HEAD_2);
        outputStream.write(5);
        outputStream.write(TYPE_D_READY_TO_TRANSFER);
        LogTag.log(UpgradePotocol.class.getName(), "readyToTransfer fileLength : " + fileLength);
        byte[] fileLengthBytes = PduParser.int2Byte(fileLength);
        for (byte fileLengthByte : fileLengthBytes) {
            LogTag.log(UpgradePotocol.class.getName(), "readyToTransfer fileLengthByte : " + fileLengthByte);
            outputStream.write(fileLengthByte);
        }
        outputStream.write(calculateCheckSumWithType(TYPE_D_READY_TO_TRANSFER, fileLengthBytes));
        return outputStream.toByteArray();
    }

    public static byte[] transferData(int number, int size, byte[] data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FRAME_HEAD_1);
        outputStream.write(FRAME_HEAD_2);
        outputStream.write(1 + 4 + 1 + size);
        outputStream.write(TYPE_D_TRANSFER_DATA);
        byte[] fileLengthBytes = PduParser.int2Byte(number);
        for (byte fileLengthByte : fileLengthBytes) {
            outputStream.write(fileLengthByte);
        }
        outputStream.write(size);
        for (int i = 0; i < size; i++) {
            outputStream.write(data[i]);
        }
        outputStream.write(calculateCheckSumWithType(TYPE_D_TRANSFER_DATA + size, fileLengthBytes, data));
        return outputStream.toByteArray();
    }

    public static byte[] transferFinish(int fileCheckSum) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FRAME_HEAD_1);
        outputStream.write(FRAME_HEAD_2);
        outputStream.write(5);
        outputStream.write(TYPE_D_TRANSFER_FINISH);
        byte[] fileLengthBytes = PduParser.int2Byte(fileCheckSum);
        for (byte fileLengthByte : fileLengthBytes) {
            outputStream.write(fileLengthByte);
        }
        outputStream.write(calculateCheckSumWithType(TYPE_D_TRANSFER_FINISH, fileLengthBytes));
        return outputStream.toByteArray();
    }

    public static byte[] runFirmware() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FRAME_HEAD_1);
        outputStream.write(FRAME_HEAD_2);
        outputStream.write(2);
        outputStream.write(TYPE_D_RUN_FIRMWARE);
        outputStream.write(0);
        outputStream.write(calculateCheckSum(TYPE_D_RUN_FIRMWARE));
        return outputStream.toByteArray();
    }

    public static byte[] reboot() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FRAME_HEAD_1);
        outputStream.write(FRAME_HEAD_2);
        outputStream.write(2);
        outputStream.write(TYPE_D_REBOOT_BOARD);
        outputStream.write(0);
        outputStream.write(calculateCheckSum(0));
        return outputStream.toByteArray();
    }

    private static int calculateCheckSum(int...data) {
        int sum = 0;
        for (int dataByte : data) {
            sum += dataByte;
        }
        return ~sum & 0xFF;
    }

    private static int calculateCheckSumWithType(int type, byte[]... bytes) {
        int sum = type;
        for (byte[] aByte : bytes) {
            for (byte dataByte : aByte) {
                sum += PduParser.oneByte2Int(dataByte);
            }
        }
        return ~sum & 0xFF;
    }
}
