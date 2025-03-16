package com.example.chess_mobile.logic.game_states;

public enum Player {
    NONE,
    WHITE,
    BLACK;
    
    public Player getOpponent() {
        switch (this) {
            case WHITE: return BLACK;
            case BLACK: return WHITE;
            default: return NONE;
        }
    }
}