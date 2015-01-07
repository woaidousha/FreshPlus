package com.air.board.service;

import com.air.lib.communication.utils.AirConstant;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    public WebSocketServer() throws UnknownHostException {
        super(new InetSocketAddress(AirConstant.WEB_SOCKET_PORT));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}
