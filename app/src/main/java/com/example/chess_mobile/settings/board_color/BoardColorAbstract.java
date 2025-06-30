package com.example.chess_mobile.settings.board_color;

import android.graphics.Color;

import com.example.chess_mobile.model.logic.moves.Move;

public class BoardColorAbstract implements IBoardColorTheme {
    private int blackCell;
    private int whiteCell;
    private int selectedPositionCell;
    private int lastMoveCell;
    private int highlightCell;
    private int highlightCaptureCell;

    public BoardColorAbstract () {
        blackCell = Color.parseColor("#4E4E4E");
        whiteCell = Color.parseColor("#FAFAFA");

        selectedPositionCell = Color.parseColor("#FFD54F");
        lastMoveCell = Color.parseColor("#FFEB3B");
        highlightCell = Color.parseColor("#4CAF50");
        highlightCaptureCell = Color.parseColor("#F44336");
    }

    @Override
    public int getLastMoveCellColor() {
        return lastMoveCell;
    }

    @Override
    public int getSelectedCellHighlightColor() {
        return selectedPositionCell;
    }

    @Override
    public int getCellHighlightColor(Move move) {
        return move.isCapture() ? highlightCaptureCell : highlightCell;
    }

    @Override
    public int getCellBackgroundColor(boolean isWhite) {
        return isWhite ? blackCell : whiteCell;
    }


    @Override
    public int getCellHighlightCapturedColor() {
        return highlightCaptureCell;
    }

    @Override
    public int getCellHighlightColor() {
        return highlightCell;
    }
    @Override
    public void setLastMoveCellColor(int color) {
        lastMoveCell = color;
    }

    @Override
    public void setSelectedCellHighlightColor(int color) {
        selectedPositionCell = color;
    }

    @Override
    public void setCellHighlightCapturedMoveColor(int color) {
        highlightCaptureCell = color;
    }

    @Override
    public void setCellHighlightColor(int color) {
        highlightCell = color;
    }

    @Override
    public void setCellBackgroundColor(int color, boolean isWhite) {

    }
}
