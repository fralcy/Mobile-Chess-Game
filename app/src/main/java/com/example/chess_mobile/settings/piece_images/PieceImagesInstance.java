package com.example.chess_mobile.settings.piece_images;

import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.Piece;

public class PieceImagesInstance implements IPieceImagesTheme {
    private IPieceImagesTheme pieceImages;

    private PieceImagesInstance() {
        pieceImages = new PieceImagesClassic();
    }

    private void setPieceImagesTheme(EPieceImageTheme theme) {
        switch (theme) {
            case CLASSIC -> pieceImages = new PieceImagesClassic();
            case COMIC -> pieceImages = new PieceImagesComic();
            default -> pieceImages = new PieceImagesAbstract();
        }
    }

    @Override
    public int getImage(EPlayer player, EPieceType pieceType) {
        return pieceImages.getImage(player, pieceType);
    }

    @Override
    public int getImage(Piece piece) {
        return pieceImages.getImage(piece);
    }

    // Bill Pugh Singleton Design Pattern
    private static final class _instanceHolder {
        private static final PieceImagesInstance _instance = new PieceImagesInstance();
    }

    public static PieceImagesInstance getInstance() {
        return _instanceHolder._instance;
    }

    public static void setTheme(EPieceImageTheme theme) {
        getInstance().setPieceImagesTheme(theme);
    }
}
