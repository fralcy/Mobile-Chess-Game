package com.example.chess_mobile.utils.implementations.piece_images;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.pieces.EPieceType;
import com.example.chess_mobile.model.pieces.Piece;
import com.example.chess_mobile.utils.interfaces.IPieceImages;

public class PieceImagesInstance implements IPieceImages {
    private static IPieceImages _instance;
    private PieceImagesInstance() {
        _instance = new PieceImagesClassic();
    }
//
//    private PieceImagesInstance(EPieceImageTheme theme) {
//        switch (theme) {
//            case CLASSIC -> _instance = new PieceImagesClassic();
//            default -> _instance = new PieceImagesAbstract();
//        }
//    }

    @Override
    public int getImage(EPlayer player, EPieceType pieceType) {
        return _instance.getImage(player, pieceType);
    }
    @Override
    public int getImage (Piece piece) {
        return _instance.getImage(piece);
    }
    public static IPieceImages getInstance() {
        if (_instance == null)
            _instance = new PieceImagesClassic();

        return _instance;
    }
}
