package com.example.chess_mobile.settings.board_color;

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
    public void setLastMoveCellColor(int color) {
        getInstance().boardColorTheme.setLastMoveCellColor(color);
    }

    @Override
    public void setSelectedCellHighlightColor(int color) {
        getInstance().boardColorTheme.setSelectedCellHighlightColor(color);
    }

    @Override
    public int getCellHighlightCapturedColor() {
        return getInstance().boardColorTheme.getCellHighlightCapturedColor();
    }

    @Override
    public int getCellHighlightColor() {
        return getInstance().boardColorTheme.getCellHighlightColor();
    }

    @Override
    public void setCellHighlightCapturedMoveColor(int color) {
        getInstance().boardColorTheme.setCellHighlightCapturedMoveColor(color);
    }

    @Override
    public void setCellHighlightColor(int color) {
        getInstance().boardColorTheme.setCellHighlightColor(color);
    }

    @Override
    public void setCellBackgroundColor(int color, boolean isWhite) {
        getInstance().boardColorTheme.setCellBackgroundColor(color, isWhite);
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
