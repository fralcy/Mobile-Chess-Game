package com.example.chess_mobile.theme.board_color;

import com.example.chess_mobile.model.logic.moves.Move;

public interface IBoardColorTheme {
    public int getLastMoveCellColor();
    public int getSelectedCellHighlightColor();
    public int getCellHighlightColor(Move move);
    public int getCellBackgroundColor(boolean isWhite);
}
