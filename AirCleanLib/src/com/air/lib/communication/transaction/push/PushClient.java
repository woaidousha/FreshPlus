package com.air.lib.communication.transaction.push;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.air.lib.communication.utils.LogTag;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class PushClient extends WebSocketClient {

    public static final int MSG_WEBSOCKET_OPEN = 0x9001;
    public static final int MSG_WEBSOCKET_MESSAGE = 0x9002;
    public static final int MSG_WEBSOCKET_CLOSE = 0x9003;
    public static final int MSG_WEBSOCKET_ERROR = 0x9004;
    public static final int MSG_WEBSOCKET_PONG = 0x9005;

    private static final String TAG = "PushClient";

    private Handler mHandler;


    public PushClient(Context context, URI serverURI, Handler handler) {
        super(serverURI);
        mHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_WEBSOCKET_OPEN);
        }
    }

    @Override
    public void onMessage(String message) {
        if (mHandler != null) {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_WEBSOCKET_MESSAGE, message));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogTag.log(TAG, "code : " + code + ", reason : " + reason + ", remote : " + remote);
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_WEBSOCKET_CLOSE);
        }
    }

    @Override
    public void onError(Exception ex) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_WEBSOCKET_ERROR);
        }
    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_WEBSOCKET_PONG);
        }
    }


    public boolean isOpen() {
        WebSocket conn = getConnection();
        return ((conn != null) && conn.isOpen());
    }

}
