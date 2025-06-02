package com.example.chess_mobile.services.websocket.interfaces;

public interface IChessWebSocketClient {
    void setTopic(String topic);
    void connect(String channelEndpoint, IWebSocketMessageListenerAdapter listener);
    void sendMessage(String message);
    void close();
}
