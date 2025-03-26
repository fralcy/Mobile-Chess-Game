package com.example.chess_mobile.theme.board_color;

import com.example.chess_mobile.model.logic.moves.Move;

public class BoardColorInstance implements IBoardColorTheme{
    private IBoardColorTheme boardColorTheme;
    private BoardColorInstance() { boardColorTheme = new BoardColorAbstract(); }

    private static final class _instanceHolder {
        private static final BoardColorInstance _instance = new BoardColorInstance();
    }

    public static BoardColorInstance getInstance() {
        return BoardColorInstance._instanceHolder._instance;
    }

//    public static void setTheme(EPieceImageTheme theme) {
//        getInstance().setPieceImagesTheme(theme);
//    }

    @Override
    public int getLastMoveCellColor() {
        return getInstance().boardColorTheme.getLastMoveCellColor();
    }

    @Override
    public int getSelectedCellHighlightColor() {
        return getInstance().boardColorTheme.getSelectedCellHighlightColor();
    }

    @Override
    public int getCellHighlightColor(Move move) {
        return getInstance().boardColorTheme.getCellHighlightColor(move);
    }

    @Override
    public int getCellBackgroundColor(boolean isWhite) {
        return getInstance().boardColorTheme.getCellBackgroundColor(isWhite);
    }
}
