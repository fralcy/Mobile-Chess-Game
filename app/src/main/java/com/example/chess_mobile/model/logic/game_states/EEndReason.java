package com.example.chess_mobile.model.logic.game_states;

public enum EEndReason {
    DRAW,
    CHECKMATE,
    RESIGNATION,
    STALEMATE,
    FIFTY_MOVE_RULE,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    TIMEOUT
}