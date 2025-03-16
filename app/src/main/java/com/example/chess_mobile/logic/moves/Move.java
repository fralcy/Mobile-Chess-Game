package com.example.chess_mobile.logic.moves;

import com.example.chess_mobile.logic.game_states.Board;
import com.example.chess_mobile.logic.game_states.Player;
import com.example.chess_mobile.logic.game_states.Position;

public abstract class Move {
    public abstract MoveType getType();
    public abstract Position getFromPos();
    public abstract Position getToPos();
    
    public boolean execute(Board board) {
        return false;
    }
    
    public boolean isLegal(Board board) {
        Player player = board.getPiece(getFromPos()).getColor();
        Board boardCopy = board.copy();
        execute(boardCopy);
        return !boardCopy.isInCheck(player);
    }
}