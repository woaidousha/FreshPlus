package com.air.board.pdu;

import com.air.lib.communication.utils.LogTag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PduParser {

    private static final String TAG = "PduParser";

    public static final String HEADER_STRING = "AA55";

    public static final int FRAME_HEAD_1 = 0xAA;
    public static final int FRAME_HEAD_2 = 0x55;

    public static final int TYPE_WORK_MODE_SWITCH = 0x00;
    public static final int TYPE_SEND_STATUS_CHANGE = 0x01;
    public static final int TYPE_REC_INIT_STATUS_CMD = 0x02;
    public static final int TYPE_REC_ALERT = 0x03;
    public static final int TYPE_SEND_MOTOR_SPEED = 0x04;
    public static final int TYPE_SEND_LIGHT_COLOR = 0x05;
    public static final int TYPE_REC_ARM_STATUS = 0x06;
    public static final int TYPE_REPORT_TEMPERATURE = 0x10;
    public static final int TYPE_REPORT_HUMIDITY = 0x11;
    public static final int TYPE_REPORT_LIGHT = 0x12;
    public static final int TYPE_REPORT_PM_2_5 = 0x13;
    public static final int TYPE_REPORT_VOC = 0x14;
    public static final int TYPE_REPORT_MOTOR1_SPEED = 0x15;
    public static final int TYPE_REPORT_MOTOR2_SPEED = 0x16;
    public static final int TYPE_REPORT_FILTER_ID = 0x20;
    public static final int TYPE_REPORT_FILTER_USE_TIME = 0x21;
    public static final int TYPE_REPORT_FILTER_LIFE = 0x22;
    public static final int TYPE_REPORT_BOARD_ID = 0xF0;
    public static final int TYPE_SEND_FIREWARE_UPGRADE = 0xFF;

    public static final int VALUE_WORK_MODE_STANDY = 0x00;
    public static final int VALUE_WORK_MODE_AUTO = 0x01;
    public static final int VALUE_WORK_MODE_FAST = 0x02;

    public static final int VALUE_STATUS_BOOT_COMPLETE = 0x00;
    public static final int VALUE_STATUS_INITING = 0x01;
    public static final int VALUE_STATUS_CONNECTED_SERVER = 0x02;
    public static final int VALUE_STATUS_CONNECTED_SERVER_FAILED = 0x03;

    public static final int VALUE_REC_INIT_STATUS_CMD = 0x00;

    public static final int VALUE_ALERT_DOOR_OPEN = 0x00;
    public static final int VALUE_ALERT_FILTER_ERROR = 0x01;
    public static final int VALUE_ALERT_MOTOR_ERROR = 0x03;
    public static final int VALUE_ALERT_SENSOR_ERROR = 0x04;

    public static final int VALUE_FIREWARE_UPGRADE = 0x00;

    private byte[] mPduBytes;

    public PduParser(byte[] pduBytes) {
        mPduBytes = pduBytes;
    }

    public PduParser(String pduString) {
        mPduBytes = pduString.getBytes();
    }

    public ArrayList<GenericPdu> parser() {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte mPduByte : mPduBytes) {
            stringBuilder.append(String.format("%02x", mPduByte));
            ;
        }
        String byteString = stringBuilder.toString().toUpperCase();
        LogTag.log(TAG, "byteString : " + byteString);
        int firstHeadPos = byteString.indexOf(HEADER_STRING);
        if (firstHeadPos < 0) {
            return null;
        }
        byteString = byteString.substring(firstHeadPos);
        LogTag.log(TAG, "sub byteString : " + byteString);
        String[] pdus = byteString.split(HEADER_STRING);
        ArrayList<byte[]> pduBytes = new ArrayList<byte[]>();
        for (String pdu : pdus) {
            if (pdu == null || pdu == "") {
                continue;
            }
            String eachPdu = HEADER_STRING + pdu;
            LogTag.log(TAG, "eachPdu : " + eachPdu);
            pduBytes.add(hexStringToBytes(eachPdu));
        }

        ArrayList<GenericPdu> pduList = new ArrayList<GenericPdu>();
        for (byte[] pduByteArray : pduBytes) {
            GenericPdu genericPdu = parsePdu(pduByteArray);
            if (genericPdu != null) {
                pduList.add(genericPdu);
            }
        }
        return pduList;
    }

    private GenericPdu parsePdu(byte[] pduByteArray) {
        GenericPdu pdu = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pduByteArray);
        try {
            int header1 = extractByteValue(inputStream);
            if (header1 != FRAME_HEAD_1) {
                return pdu;
            }
            int header2 = extractByteValue(inputStream);
            if (header2 != FRAME_HEAD_2) {
                return pdu;
            }
            int dataLength = extractByteValue(inputStream);
            LogTag.log(TAG, "dataLength : " + dataLength);
            int type = extractByteValue(inputStream);
            LogTag.log(TAG, "type int:" + type);
            pdu = createPduByType(type);
            LogTag.log(TAG, "pdu class:" + (pdu == null ? "null" : pdu.getClass().getName()));
            if (pdu == null) {
                return null;
            }
            pdu.setDataLengthLength(dataLength);
            int[] params = new int[dataLength - 1];
            LogTag.log(TAG, "getNeedParamsLength :" + pdu.getNeedParamsLength() + ", params : " + params.length);
            LogTag.log(TAG, "type and param: " + pdu.getClass().getName() + ",params :" + Arrays.toString(params));
            if (pdu.getNeedParamsLength() != params.length) {
                return null;
            }
            pdu.setPackageBytes(pduByteArray);
            pdu.setType(type);
            for (int i = 0; i < params.length; i++) {
                params[i] = extractByteValue(inputStream);
            }
            pdu.setParams(params);
            int checkSum = extractByteValue(inputStream);
            int calculateCheckSum = pdu.calculateCheckSum();
            LogTag.log(TAG, "calculateCheckSum : " + calculateCheckSum);
            if (checkSum == calculateCheckSum) {
                pdu.setCheckSum(checkSum);
            } else {
                return null;
            }
            LogTag.log(TAG, "type : " + pdu.getClass().getName() + ",result :" + pdu.getResult());
        } catch (IllegalStateException e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pdu;
    }

    private GenericPdu createPduByType(int type) {
        GenericPdu pdu = null;
        switch (type) {
            case TYPE_WORK_MODE_SWITCH:
                pdu = new WorkModePdu();
                break;
            case TYPE_REC_INIT_STATUS_CMD:
                pdu = new InitPdu();
                break;
            case TYPE_REC_ALERT:
                pdu = new AlertPdu();
                break;
            case TYPE_REC_ARM_STATUS:
                pdu = new ARMStatusPdu();
                break;
            case TYPE_REPORT_TEMPERATURE:
                pdu = new TemperaturePdu();
                break;
            case TYPE_REPORT_HUMIDITY:
                pdu = new HumidityPdu();
                break;
            case TYPE_REPORT_LIGHT:
                pdu = new LightPdu();
                break;
            case TYPE_REPORT_PM_2_5:
                pdu = new PM25Pdu();
                break;
            case TYPE_REPORT_VOC:
                pdu = new VocPdu();
                break;
            case TYPE_REPORT_MOTOR1_SPEED:
            case TYPE_REPORT_MOTOR2_SPEED:
                pdu = new MotorSpeedPdu(type == TYPE_REPORT_MOTOR1_SPEED);
                break;
            case TYPE_REPORT_FILTER_ID:
                pdu = new FilterIdPdu();
                break;
            case TYPE_REPORT_FILTER_USE_TIME:
                pdu = new FilterUsedPdu();
                break;
            case TYPE_REPORT_FILTER_LIFE:
                pdu = new FilterLifePdu();
                break;
            case TYPE_REPORT_BOARD_ID:
                pdu = new BoardIdPdu();
                break;
        }
        return pdu;
    }

    public static int extractByteValue(ByteArrayInputStream pduStream) {
        assert (null != pduStream);
        if (pduStream.available() <= 0) {
            throw new IllegalStateException("stream is not available");
        }
        int temp = pduStream.read();
        return temp & 0xFF;
    }

    public static String toHexString(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(toHex(inputStream.read() >> 4));
            sb.append(toHex(inputStream.read()));
        }
        return sb.toString();
    }

    private static char toHex(int nibble) {
        final char[] hexDigit =
                {
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
                };
        return hexDigit[nibble & 0xF];
    }

    static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);
        return 0;
    }

    public static byte[] hexStringToBytes(String s) {
        byte[] ret;

        if (s == null) return null;

        int sz = s.length();

        ret = new byte[sz / 2];

        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                    | hexCharToInt(s.charAt(i + 1)));
        }

        return ret;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static byte[] int2Byte(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
        }
        return b;
    }

    public static int oneByte2Int(byte byteNum) {
        return byteNum > 0 ? byteNum : (128 + (128 + byteNum));
    }

    public static long unsigned4BytesToInt(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;
        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        thirdByte = (0x000000FF & ((int) buf[index + 2]));
        fourthByte = (0x000000FF & ((int) buf[index + 3]));
        index = index + 4;
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }
}