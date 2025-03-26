package com.example.chess_mobile.utils.interfaces;

import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.pieces.EPieceType;
import com.example.chess_mobile.model.pieces.Piece;

public interface IPieceImages {
    int getImage(EPlayer player, EPieceType pieceType);
    int getImage(Piece piece);

}
