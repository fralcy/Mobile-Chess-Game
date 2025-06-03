package com.example.chess_mobile.services.websocket.implementations;

import com.example.chess_mobile.services.websocket.interfaces.IChessWebSocketClient;
import com.example.chess_mobile.services.websocket.interfaces.IWebSocketMessageListenerAdapter;

public class ChessWebSocketStompClient implements IChessWebSocketClient {
    private String _channelTopicEndpoint;

    @Override
    public void setTopic(String topic) {
        this._channelTopicEndpoint = topic;
    }

    @Override
    public void connect(String channelEndpoint, IWebSocketMessageListenerAdapter listener) {
        SocketManager.getInstance().subscribeTopic("/topic" + this._channelTopicEndpoint, stompMessage -> {
            listener.onMessageReceived(stompMessage.getPayload());
        });
    }

    @Override
    public void sendMessage(String message) {
        SocketManager.getInstance().sendMessage(message, "/app" + this._channelTopicEndpoint);
    }

    @Override
    public void close() {
        SocketManager.getInstance().unsubscribeTopic(this._channelTopicEndpoint);
        SocketManager.getInstance().disconnect();
    }
}
