package com.example.chess_mobile.model.moves;

public enum EMoveType {
    NORMAL,
    CASTLE_KS,  // Castle king's side
    CASTLE_QS,  // Castle queen's side
    DOUBLE_PAWN, // Double pawn move
    EN_PASSANT, // En passant
    PAWN_PROMOTION // Pawn promotion
}