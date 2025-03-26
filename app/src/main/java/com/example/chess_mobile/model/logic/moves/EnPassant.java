package com.example.chess_mobile.model.logic.moves;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.Position;

public class EnPassant extends Move {
    private final Position fromPos;
    private final Position toPos;
    private final Position capturePos;
    
    public EnPassant(Position from, Position to) {
        this.fromPos = from;
        this.toPos = to;
        this.capturePos = new Position(from.row(), to.column());
    }
    
    @Override
    public EMoveType getType() {
        return EMoveType.EN_PASSANT;
    }
    
    @Override
    public Position getFromPos() {
        return fromPos;
    }
    
    @Override
    public Position getToPos() {
        return toPos;
    }
    
    @Override
    public boolean execute(Board board) {
        new NormalMove(fromPos, toPos).execute(board);
        board.setPiece(capturePos, null);
        
        return true;
    }
}