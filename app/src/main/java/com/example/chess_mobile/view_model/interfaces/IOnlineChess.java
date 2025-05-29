package com.example.chess_mobile.view_model.interfaces;

import androidx.lifecycle.MutableLiveData;

import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.view_model.enums.ESocketMessageType;

public interface IOnlineChess {
    void setOnlineStatus(boolean status);
    void onGameConnection(String gameID);
    void onGameDisconnection();

    MutableLiveData<Boolean> getWebSocketStatus();
    void handleSocketMessage(ESocketMessageType messageType, GameState gameState);
    void sendMessage(ESocketMessageType messageType, GameState gameState);
    void handleIncomingMessage(String message);
}
