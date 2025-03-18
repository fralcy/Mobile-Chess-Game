package com.example.chess_mobile.model.moves;

import com.example.chess_mobile.model.game_states.Board;
import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.game_states.Position;

public abstract class Move {
    public abstract EMoveType getType();
    public abstract Position getFromPos();
    public abstract Position getToPos();
    
    public boolean execute(Board board) {
        return false;
    }
    
    public boolean isLegal(Board board) {
        EPlayer player = board.getPiece(getFromPos()).getColor();
        Board boardCopy = board.copy();
        execute(boardCopy);
        return !boardCopy.isInCheck(player);
    }
}