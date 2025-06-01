package com.example.chess_mobile.model.websocket.interfaces;

public interface IChessWebSocketClient {
    void connect(String channelEndpoint, IWebSocketMessageListenerAdapter listener);
    void sendMessage(String message);
    void close();
}
