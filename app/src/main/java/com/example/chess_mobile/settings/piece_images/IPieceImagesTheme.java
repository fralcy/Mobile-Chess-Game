package com.example.chess_mobile.settings.piece_images;

import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.Piece;

public interface IPieceImagesTheme {
    int getImage(EPlayer player, EPieceType pieceType);
    int getImage(Piece piece);

}
