package com.example.chess_mobile.logic.game_states;

public enum Player {
    NONE,
    WHITE,
    BLACK;
    
    public Player getOpponent() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            default -> NONE;
        };
    }
}