package com.example.chess_mobile.model.websocket;

public interface IWebSocketMessageListener {
    void onMessageReceived(String message);
}