package com.air.lib.communication.utils;

import android.net.wifi.ScanResult;
import com.air.lib.communication.data.BasePushCmd;
import com.air.lib.communication.data.WebSocketServerInfo;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.data.WifiInfoList;
import com.air.lib.communication.transaction.push.PushConstant;
import com.air.lib.communication.transaction.push.PushMessage;
import com.google.gson.Gson;
import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;

public class PushSocketUtil {

    public static String sDeviceUuid = "";

    public static int createMessageId() {
        return  UUID.randomUUID().hashCode();
    }

    public static PushMessage genPushMessage(BasePushCmd message) {
        PushMessage pushMessage = new PushMessage();
        pushMessage.setId(createMessageId());
        PushMessage.Params params = new PushMessage.Params();
        params.setDevice(sDeviceUuid);
        params.setMessage(message.getJson());
        pushMessage.setParams(params);
        return pushMessage;
    }

    public static PushMessage genShakeHandMessage(String deviceUuid) {
        PushMessage pushMessage = new PushMessage();
        pushMessage.setId(PushConstant.MESSAGE_ID_CONNECT);
        pushMessage.setCommand(PushMessage.Command.CONNECT);
        PushMessage.Params params = new PushMessage.Params();
        params.setDevice(deviceUuid);
        pushMessage.setParams(params);
        return pushMessage;
    }

    public static PushMessage createResponseMessage(long messageId) {
        PushMessage pushMessage = new PushMessage();
        pushMessage.setId(messageId);
        pushMessage.setType(PushMessage.MsgType.TYPE_RECEIVE);
        pushMessage.setResult(PushMessage.MsgResult.RESULT_SUCCESS);
        return pushMessage;
    }

    public static PushMessage createWebSocketServerInfo() {
        Gson gson = new Gson();
        WebSocketServerInfo webSocketServerInfo = new WebSocketServerInfo();
        webSocketServerInfo.setIp(getLocalIpAddress());
        BasePushCmd basePushCmd = new BasePushCmd();
        basePushCmd.setType(BasePushCmd.TYPE_RES_WEB_SOCKET_SERVER_INFO);
        basePushCmd.setMsg(gson.toJson(webSocketServerInfo));
        return genPushMessage(basePushCmd);
    }

    public static PushMessage createReqWifiList() {
        BasePushCmd basePushCmd = new BasePushCmd();
        basePushCmd.setType(BasePushCmd.TYPE_REQ_WIFI_LIST);
        return genPushMessage(basePushCmd);
    }

    public static PushMessage createWifiInfoList(ArrayList<ScanResult> scanResults) {
        Gson gson = new Gson();
        WifiInfoList wifiInfoList = new WifiInfoList();
        wifiInfoList.setScanResults(scanResults);
        BasePushCmd basePushCmd = new BasePushCmd();
        basePushCmd.setType(BasePushCmd.TYPE_RES_WIFI_LIST);
        basePushCmd.setMsg(gson.toJson(wifiInfoList));
        return genPushMessage(basePushCmd);
    }

    public static PushMessage createWifiConfig(WifiConfig config) {
        Gson gson = new Gson();
        BasePushCmd basePushCmd = new BasePushCmd();
        basePushCmd.setType(BasePushCmd.TYPE_REQ_WIFI_INFO);
        basePushCmd.setMsg(gson.toJson(config));
        return genPushMessage(basePushCmd);
    }

    public static PushMessage createEmptyMsgByType(int type) {
        BasePushCmd basePushCmd = new BasePushCmd();
        basePushCmd.setType(type);
        return genPushMessage(basePushCmd);
    }

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if (!mInetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(
                            mInetAddress.getHostAddress())) {
                        return mInetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogTag.e("", "init host ip failed", ex);
        }
        return null;
    }
}
