package com.example.chess_mobile.model.logic.game_states;

public enum EEndReason {
    CHECKMATE,
    STALEMATE,
    FIFTY_MOVE_RULE,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    TIMEOUT
}