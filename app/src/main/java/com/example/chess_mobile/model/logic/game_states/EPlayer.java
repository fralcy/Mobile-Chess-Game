package com.example.chess_mobile.model.logic.game_states;

public enum EPlayer {
    NONE,
    WHITE,
    BLACK;
    
    public EPlayer getOpponent() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            default -> NONE;
        };
    }
}