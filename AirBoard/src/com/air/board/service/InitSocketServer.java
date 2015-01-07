package com.air.board.service;

import com.air.lib.communication.data.BasePushCmd;
import com.air.lib.communication.data.WifiConfig;
import com.air.lib.communication.transaction.push.PushMessage;
import com.air.lib.communication.transaction.push.PushMessageManager;
import com.air.lib.communication.utils.AirConstant;
import com.air.lib.communication.utils.LogTag;
import com.air.lib.communication.utils.PushSocketUtil;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public class InitSocketServer extends org.java_websocket.server.WebSocketServer {

    private static final String TAG = "InitSocketServer";

    private BoardService mBoardService;
    private Gson mGson;
    private OnWifiConfigListener mWifiConfigListener;

    public interface OnWifiConfigListener {
        public void onWifiConfig(WifiConfig config);
    }

    public void setWifiConfigListener(OnWifiConfigListener listener) {
        this.mWifiConfigListener = listener;
    }

    public InitSocketServer(BoardService boardService) throws UnknownHostException {
        super(new InetSocketAddress(AirConstant.WEB_SOCKET_PORT));
        this.mBoardService = boardService;
        mGson = new Gson();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogTag.log(TAG, conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogTag.log(TAG, conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        PushMessage pushMessage = PushMessage.fromJson(message);
        switch (pushMessage.getType()) {
            case PushMessage.MsgType.TYPE_RECEIVE: {
                PushMessageManager.getInstance().removeSentMessage(pushMessage.getId());
                break;
            }
            case PushMessage.MsgType.TYPE_SEND: {
                conn.send(PushSocketUtil.createResponseMessage(pushMessage.getId()).toJson());
                BasePushCmd basePushCmd = BasePushCmd.fromJson(pushMessage.getParams().getMessage());
                PushMessage response = parseMsg(basePushCmd);
                if (response != null) {
                    LogTag.log(TAG, "response : " + response.toJson());
                    conn.send(response.toJson());
                }
                break;
            }
        }
        LogTag.log(TAG, conn + ": " + message);
    }

    private PushMessage parseMsg(BasePushCmd basePushCmd) {
        LogTag.log(TAG, "parseMsg : " + basePushCmd.toString());
        PushMessage pushMessage = null;
        switch (basePushCmd.getType()) {
            case BasePushCmd.TYPE_REQ_WIFI_LIST: {
                LogTag.log(TAG, "parseMsg TYPE_REQ_WIFI_LIST");
                pushMessage = PushSocketUtil.createWifiInfoList(mBoardService.getScanResults());
                break;
            }
            case BasePushCmd.TYPE_REQ_WIFI_INFO: {
                LogTag.log(TAG, "parseMsg TYPE_REQ_WIFI_INFO");
                pushMessage = PushSocketUtil.createEmptyMsgByType(BasePushCmd.TYPE_RES_WIFI_INFO);
                WifiConfig wifiConfig = WifiConfig.fromJson(basePushCmd.getMsg());
                if (mWifiConfigListener != null) {
                    mWifiConfigListener.onWifiConfig(wifiConfig);
                }
                break;
            }
        }
        return pushMessage;
    }

    public void onFragment(WebSocket conn, Framedata fragment) {
        LogTag.log(TAG, "received fragment: " + fragment);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.
     *
     * @param text The String to send across the network.
     * @throws InterruptedException When socket related I/O errors occur.
     */
    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }
}
