package com.example.chess_mobile.model.logic.moves;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;

public abstract class Move {
    private boolean isCapture = false;
    public abstract EMoveType getType();
    public abstract Position getFromPos();
    public abstract Position getToPos();

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        this.isCapture = capture;
    }

    public boolean execute(Board board) {
        return false;
    }
    
    public boolean isLegal(Board board) {
        EPlayer player = board.getPiece(getFromPos()).getPlayerColor();
        Board boardCopy = board.copy();
        execute(boardCopy);
        return !boardCopy.isInCheck(player);
    }
}