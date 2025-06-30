package com.example.chess_mobile.settings.board_color;

import com.example.chess_mobile.model.logic.moves.Move;

public interface IBoardColorTheme {
    int getLastMoveCellColor();
    int getSelectedCellHighlightColor();
    int getCellHighlightColor(Move move);
    int getCellHighlightColor();
    int getCellHighlightCapturedColor();
    int getCellBackgroundColor(boolean isWhite);


    void setLastMoveCellColor(int color);
    void setSelectedCellHighlightColor(int color);
    void setCellHighlightColor(int color);
    void setCellHighlightCapturedMoveColor(int color);
    void setCellBackgroundColor(int color, boolean isWhite);
}
