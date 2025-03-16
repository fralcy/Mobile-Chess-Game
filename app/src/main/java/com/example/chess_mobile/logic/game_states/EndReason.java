package com.example.chess_mobile.logic.game_states;

public enum EndReason {
    CHECKMATE,
    STALEMATE,
    FIFTY_MOVE_RULE,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    TIMEOUT
}