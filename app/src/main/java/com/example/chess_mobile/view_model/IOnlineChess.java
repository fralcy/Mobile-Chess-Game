package com.example.chess_mobile.view_model;

import com.example.chess_mobile.model.logic.game_states.GameState;

public interface IOnlineChess {
    void onGameConnection(String gameID);
    void onGameDisconnection();
    void handleSocketMessage(ESocketMessageType messageType, GameState gameState);
    void sendMessage(ESocketMessageType messageType, GameState gameState);
}
