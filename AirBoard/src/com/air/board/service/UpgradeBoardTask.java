package com.air.board.service;

import android.os.AsyncTask;
import android.os.Environment;
import com.air.board.BoardApplication;
import com.air.board.pdu.PduParser;
import com.air.board.pdu.UpgradePdu;
import com.air.board.pdu.UpgradePotocol;
import com.air.lib.communication.utils.LogTag;
import com.example.serial.util.SerialLib;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeBoardTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "UpgradeTask";

    private static final long TIMER_DELAY = 5 * 1000;
    private static final int DATA_LENGTH = 240;

    private Timer mTimer;
    private int mFd;
    private int mCurrentDataType = UpgradePotocol.TYPE_D_CONNECT_BOARD;
    private boolean mIsTimeOut = false;

    public UpgradeBoardTask() {
        mFd = BoardApplication.getApplication().getSerialFd();
    }

    private void setTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = new Timer();
        LogTag.log(TAG, "set timer");
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogTag.log(TAG, "time out and cancel task");
                mIsTimeOut = true;
            }
        }, TIMER_DELAY);
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        UpgradePdu upgradePdu = new UpgradePdu();
        SerialLib.sendData(mFd, upgradePdu.buildPdu(PduParser.VALUE_FIREWARE_UPGRADE));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] buff = null;
        boolean finish = false;
        boolean success = false;
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "firmware.bin");
        LogTag.log(TAG, "file path :" + file.getPath());
        if (file == null || !file.exists()
                || file.length() % 4 != 0) {
            LogTag.log(TAG, "return because firmware error");
            return success;
        }
        byte[] fileBytes = null;
        int mCurrentLength = 0;
        InputStream in = null;
        int number = 0;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while ((buff = SerialLib.recvData(mFd)) != null
                || buff.length != 0) {
            LogTag.log(TAG, "clear data");
        }
        buff = null;
        while (!finish) {
            setTimer();
            if (mIsTimeOut) {
                return success;
            }
            LogTag.log(TAG, "current data type : " + mCurrentDataType);
            int timeLength = 0;
            switch (mCurrentDataType) {
                case UpgradePotocol.TYPE_D_CONNECT_BOARD:
                    LogTag.log(TAG, "create upgrade pdu : TYPE_D_CONNECT_BOARD");
                    buff = UpgradePotocol.connectBoard();
                    break;
                case UpgradePotocol.TYPE_D_READY_TO_TRANSFER:
                    LogTag.log(TAG, "create upgrade pdu : TYPE_D_READY_TO_TRANSFER");
                    buff = UpgradePotocol.readyToTransfer((int) file.length());
                    break;
                case UpgradePotocol.TYPE_D_TRANSFER_DATA:
                    LogTag.log(TAG, "create upgrade pdu : TYPE_D_TRANSFER_DATA");
                    if (in != null) {
                        try {
                            fileBytes = new byte[DATA_LENGTH];
                            timeLength = in.read(fileBytes);
                            mCurrentLength += timeLength;
                            in.mark(mCurrentLength);
                            LogTag.log(TAG, "timeLength : " + timeLength);
                            if (timeLength == -1) {
                                mCurrentDataType = UpgradePotocol.TYPE_D_TRANSFER_FINISH;
                                in.close();
                                continue;
                            } else {
                                buff = UpgradePotocol.transferData(number, timeLength, fileBytes);
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            for (byte mPduByte : fileBytes) {
                                stringBuilder.append(String.format("%02x", mPduByte));
                                stringBuilder.append(" ");
                            }
                            String byteString = stringBuilder.toString().toUpperCase();
                            LogTag.log(TAG, "byteString : " + byteString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case UpgradePotocol.TYPE_D_TRANSFER_FINISH:
                    LogTag.log(TAG, "create upgrade pdu : TYPE_D_TRANSFER_FINISH");
                    assert in != null;
                    try {
                        in = new FileInputStream(file);
                        int tempByte;
                        int sum = 0;
                        while ((tempByte = in.read()) != -1) {
                            sum += tempByte;
                        }
                        buff = UpgradePotocol.transferFinish(sum);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case UpgradePotocol.TYPE_D_RUN_FIRMWARE:
                    LogTag.log(TAG, "create upgrade pdu : TYPE_D_RUN_FIRMWARE");
                    buff = UpgradePotocol.runFirmware();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            SerialLib.sendData(mFd, buff);
            if (mCurrentDataType == UpgradePotocol.TYPE_D_RUN_FIRMWARE) {
                success = true;
                break;
            }
            buff = null;
            while ((buff = SerialLib.recvData(mFd)) == null) {
                if (mIsTimeOut) {
                    return success;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogTag.log(TAG, "read recv data");
            }
            if (buff != null) {
                LogTag.log(TAG, "recv buff is not null");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buff);
                int header = PduParser.extractByteValue(inputStream);
                if (header != UpgradePotocol.FRAME_HEAD_1) {
                    continue;
                }
                header = PduParser.extractByteValue(inputStream);
                if (header != UpgradePotocol.FRAME_HEAD_2) {
                    continue;
                }
                int dataLength = PduParser.extractByteValue(inputStream);
                int type = PduParser.extractByteValue(inputStream);
                dataLength -= 1;
                int response;
                switch (type) {
                    case UpgradePotocol.TYPE_U_CONNECT_SUCCESS:
                        mCurrentDataType = UpgradePotocol.TYPE_D_READY_TO_TRANSFER;
                        break;
                    case UpgradePotocol.TYPE_U_READY_TO_TRANSFER:
                        response = PduParser.extractByteValue(inputStream);
                        if (response > 0) {
                            mCurrentDataType = UpgradePotocol.TYPE_D_TRANSFER_DATA;
                        }
                        break;
                    case UpgradePotocol.TYPE_U_DATA_RESPONSE:
                        if (dataLength == 5) {
                            byte[] numberByte = new byte[4];
                            for (int i = 0; i < 4; i++) {
                                int value = PduParser.extractByteValue(inputStream);
                                numberByte[i] = (byte) (value >> 8 * i & 0xFF);
                            }
                            response = PduParser.extractByteValue(inputStream);
                            if (response > 0) {
                                number++;
                            } else {
                                try {
                                    mCurrentLength -= timeLength;
                                    in.reset();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case UpgradePotocol.TYPE_U_CHECK_FINISH:
                        response = PduParser.extractByteValue(inputStream);
                        if (response > 0) {
                            mCurrentDataType = UpgradePotocol.TYPE_D_RUN_FIRMWARE;
                        }
                        break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        LogTag.log(TAG, "onPostExecute result :" + aBoolean);
        SerialService.setUpgrading(false);
    }
}
