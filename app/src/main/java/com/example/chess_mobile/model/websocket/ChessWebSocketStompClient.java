package com.example.chess_mobile.model.websocket;

import android.util.Log;

public class ChessWebSocketStompClient implements IChessWebSocketClient{
    private String _channelEndpoint;
    @Override
    public void connect(String channelEndpoint, IWebSocketMessageListenerAdapter listener) {
        SocketManager.getInstance().connect(() -> {
            this._channelEndpoint = channelEndpoint;
            SocketManager.getInstance().subscribeTopic(channelEndpoint, stompMessage -> listener.onMessageReceived(stompMessage.getPayload()));
        }, () ->             Log.e("ChessWebSocketStompClient", "Error while connecting the websocket"));
    }

    @Override
    public void sendMessage(String message) {
        SocketManager.getInstance().sendMessage(message, SocketManager.beEndPoint + "/app" + this._channelEndpoint);
    }

    @Override
    public void close() {
        SocketManager.getInstance().unsubscribeTopic(this._channelEndpoint);
        SocketManager.getInstance().disconnect();
    }
}
