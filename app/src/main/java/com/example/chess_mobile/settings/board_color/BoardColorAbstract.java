package com.example.chess_mobile.settings.board_color;

import android.graphics.Color;

import com.example.chess_mobile.model.logic.moves.Move;

public class BoardColorAbstract implements IBoardColorTheme {
    private final int blackCell;
    private final int whiteCell;
    private final int selectedPositionCell;
    private final int lastMoveCell;
    private final int highlightCell;
    private final int highlightCaptureCell;

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
}
