package com.example.chess_mobile.theme.piece_images;

import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.Piece;

import java.util.HashMap;
import java.util.Optional;

public class PieceImagesAbstract implements IPieceImagesTheme {
    private final HashMap<EPieceType, Integer> _black;
    private final HashMap<EPieceType, Integer> _white;

    public PieceImagesAbstract() {
        this._black = new HashMap<>();
        this._white = new HashMap<>();
    }
    @Override
    public int getImage(EPlayer player, EPieceType pieceType) {
        return switch (player) {
            case BLACK ->  this.getBlackPiece(pieceType);
            case WHITE -> this.getWhitePiece(pieceType);
            default -> 0;
        };
    }
    @Override
    public int getImage (Piece piece) {
        if (piece == null) return 0;
        return getImage(piece.getPlayerColor(), piece.getType());
    }

    public HashMap<EPieceType, Integer> getBlack() {
        return _black;
    }

    public HashMap<EPieceType, Integer> getWhite() {
        return _white;
    }

    public int getWhitePiece(EPieceType pieceType) {
        return Optional.of(getWhite())
                .map(map -> map.get(pieceType))
                .orElse(0);
    }

    public int getBlackPiece(EPieceType pieceType) {
        return Optional.of(getBlack())
                .map(map -> map.get(pieceType))
                .orElse(0);
    }

    public void addBlackPiece(EPieceType pieceType, int id) {
        this._black.put(pieceType, id);
    }

    public void addWhitePiece(EPieceType pieceType, int id) {
        this._white.put(pieceType, id);
    }
}
