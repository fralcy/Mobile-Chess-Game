package com.example.chess_mobile.logic.moves;

import com.example.chess_mobile.logic.game_states.Board;
import com.example.chess_mobile.logic.game_states.Player;
import com.example.chess_mobile.logic.game_states.Position;

public class DoublePawn extends Move {
    private final Position fromPos;
    private final Position toPos;
    private final Position skippedPos;
    
    public DoublePawn(Position from, Position to) {
        this.fromPos = from;
        this.toPos = to;
        this.skippedPos = new Position((from.getRow() + to.getRow()) / 2, from.getColumn());
    }
    
    @Override
    public MoveType getType() {
        return MoveType.DOUBLE_PAWN;
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
        Player player = board.getPiece(fromPos).getColor();
        board.setPawnSkipPosition(player, skippedPos);
        
        new NormalMove(fromPos, toPos).execute(board);
        
        return true;
    }
}