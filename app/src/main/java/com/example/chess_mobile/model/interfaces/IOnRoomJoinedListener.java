package com.example.chess_mobile.model.interfaces;

public interface IOnRoomJoinedListener {
    void onRoomJoined(String roomId);
    void onRoomJoinFailed(String error);
}