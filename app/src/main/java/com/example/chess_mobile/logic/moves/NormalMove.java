package com.example.chess_mobile.logic.moves;

import com.example.chess_mobile.logic.game_states.Board;
import com.example.chess_mobile.logic.pieces.Piece;
import com.example.chess_mobile.logic.pieces.PieceType;
import com.example.chess_mobile.logic.game_states.Position;

public class NormalMove extends Move {
    private final Position fromPos;
    private final Position toPos;
    
    public NormalMove(Position from, Position to) {
        this.fromPos = from;
        this.toPos = to;
    }
    
    @Override
    public MoveType getType() {
        return MoveType.NORMAL;
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
        Piece piece = board.getPiece(fromPos);
        boolean capture = !board.isEmpty(toPos);
        
        board.setPiece(toPos, piece);
        board.setPiece(fromPos, null);
        piece.setHasMoved(true);
        
        return capture || piece.getType() == PieceType.PAWN;
    }
}