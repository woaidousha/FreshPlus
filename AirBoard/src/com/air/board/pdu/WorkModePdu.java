package com.air.board.pdu;

import com.air.lib.communication.data.SwitchMode;

public class WorkModePdu extends SendPdu {

    @Override
    public int getNeedParamsLength() {
        return 1;
    }

    @Override
    public Integer getResult() {
        int[] params = getParams();
        int mode = params[0];
        switch (mode) {
            case PduParser.VALUE_WORK_MODE_AUTO:
                return SwitchMode.CMD_MODIFY_NORMAL_MODE;
            case PduParser.VALUE_WORK_MODE_FAST:
                return SwitchMode.CMD_MODIFY_FAST_MODE;
            case PduParser.VALUE_WORK_MODE_STANDY:
                return SwitchMode.CMD_MODIFY_STANDBY_MODE;
        }
        return params[0];
    }

    @Override
    protected int getBuildType() {
        return PduParser.TYPE_WORK_MODE_SWITCH;
    }
}
