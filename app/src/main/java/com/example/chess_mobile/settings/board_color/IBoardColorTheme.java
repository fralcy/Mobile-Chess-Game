package com.example.chess_mobile.settings.board_color;

import com.example.chess_mobile.model.logic.moves.Move;

public interface IBoardColorTheme {
    int getLastMoveCellColor();
    int getSelectedCellHighlightColor();
    int getCellHighlightColor(Move move);
    int getCellBackgroundColor(boolean isWhite);
}
