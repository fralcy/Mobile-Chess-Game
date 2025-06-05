package com.example.chess_mobile.dto.request;

import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.view_model.enums.ESocketMessageType;

public class MoveRequest {
    protected final ESocketMessageType messageType;
    protected final String currentMatchId;
    protected final GameState gameState;

    public MoveRequest(ESocketMessageType messageType, String matchId, GameState gameState) {
        this.messageType = messageType;
        this.currentMatchId = matchId;
        this.gameState = gameState;
    }

    public GameState getGameState() { return gameState; }
    public String getCurrentMatchId() { return currentMatchId; }
    public ESocketMessageType getMessageType() { return messageType; }
}
