package com.example.chess_mobile.model.moves;

import com.example.chess_mobile.model.game_states.Board;
import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.game_states.Position;

public class DoublePawn extends Move {
    private final Position fromPos;
    private final Position toPos;
    private final Position skippedPos;
    
    public DoublePawn(Position from, Position to) {
        this.fromPos = from;
        this.toPos = to;
        this.skippedPos = new Position((from.row() + to.row()) / 2, from.column());
    }
    
    @Override
    public EMoveType getType() {
        return EMoveType.DOUBLE_PAWN;
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
        EPlayer player = board.getPiece(fromPos).getPlayerColor();
        board.setPawnSkipPosition(player, skippedPos);
        
        new NormalMove(fromPos, toPos).execute(board);
        
        return true;
    }
}