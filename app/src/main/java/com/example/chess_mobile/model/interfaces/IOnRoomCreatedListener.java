package com.example.chess_mobile.model.interfaces;

public interface IOnRoomCreatedListener {
    void onRoomCreated(String roomId);
    void onRoomCreationFailed(String error);
}
