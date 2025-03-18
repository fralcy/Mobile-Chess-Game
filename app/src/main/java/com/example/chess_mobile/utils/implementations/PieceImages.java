package com.example.chess_mobile.utils.implementations;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.pieces.EPieceType;
import com.example.chess_mobile.model.pieces.Piece;

import java.util.HashMap;


public class PieceImages {
    private static PieceImages _instance;
    private final HashMap<EPieceType, Integer> _black;
    private final HashMap<EPieceType, Integer> _white;
    private PieceImages() {
        this._black = new HashMap<>();
        this._white = new HashMap<>();

        _black.put(EPieceType.ROOK, R.drawable.logo_chess);
        _black.put(EPieceType.KNIGHT, R.drawable.logo_chess);
        _black.put(EPieceType.BISHOP, R.drawable.logo_chess);
        _black.put(EPieceType.QUEEN, R.drawable.logo_chess);
        _black.put(EPieceType.KING, R.drawable.logo_chess);
        _black.put(EPieceType.PAWN, R.drawable.logo_chess);

        _white.put(EPieceType.ROOK, R.drawable.logo_chess);
        _white.put(EPieceType.KNIGHT, R.drawable.logo_chess);
        _white.put(EPieceType.BISHOP, R.drawable.logo_chess);
        _white.put(EPieceType.QUEEN, R.drawable.logo_chess);
        _white.put(EPieceType.KING, R.drawable.logo_chess);
        _white.put(EPieceType.PAWN, R.drawable.logo_chess);
    }

    public int getImage(EPlayer player, EPieceType pieceType) {
        return switch (player) {
            case BLACK ->  _black.get(pieceType);
            case WHITE -> _white.get(pieceType);
            default -> 0;
        };
    }
    public int getImage (Piece piece) {
        if (piece == null) return 0;
        return getImage(piece.getPlayerColor(), piece.getType());
    }
    public static PieceImages getInstance() {
        if (_instance == null)
            _instance = new PieceImages();

        return _instance;
    }
}
