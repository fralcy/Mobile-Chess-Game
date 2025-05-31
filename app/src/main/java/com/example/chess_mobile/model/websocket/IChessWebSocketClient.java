package com.example.chess_mobile.model.websocket;

public interface IChessWebSocketClient {
    void connect(String channelEndpoint, IWebSocketMessageListenerAdapter listener);
    void sendMessage(String message);
    void close();
}
