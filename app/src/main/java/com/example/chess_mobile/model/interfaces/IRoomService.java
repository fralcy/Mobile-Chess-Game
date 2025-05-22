package com.example.chess_mobile.model.interfaces;

import com.example.chess_mobile.model.logic.game_states.EPlayer;

public interface IRoomService {
    default void createRoom(EPlayer playerColor, int timeMinutes, IOnRoomCreatedListener listener) {
        // TODO: Implement Firebase room creation
        String roomId = generateRoomId();
        if (listener != null) {
            listener.onRoomCreated(roomId);
        }
    }

    default void joinRoom(String roomId, IOnRoomJoinedListener listener) {
        // TODO: Implement Firebase room joining
        if (roomId == null || roomId.trim().isEmpty()) {
            if (listener != null) {
                listener.onRoomJoinFailed("Invalid room ID");
            }
            return;
        }

        if (listener != null) {
            listener.onRoomJoined(roomId);
        }
    }

    default void leaveRoom(String roomId) {
        // TODO: Implement leave room functionality
    }

    default void sendMove(String roomId, String moveData) {
        // TODO: Send move data to Firebase
    }

    default String generateRoomId() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}