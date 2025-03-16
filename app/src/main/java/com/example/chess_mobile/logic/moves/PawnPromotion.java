package com.example.chess_mobile.logic.moves;

import com.example.chess_mobile.logic.game_states.Board;
import com.example.chess_mobile.logic.pieces.Piece;
import com.example.chess_mobile.logic.pieces.PieceType;
import com.example.chess_mobile.logic.game_states.Player;
import com.example.chess_mobile.logic.game_states.Position;
import com.example.chess_mobile.logic.pieces.*;

public class PawnPromotion extends Move {
    private final Position fromPos;
    private final Position toPos;
    private final PieceType newType;
    
    public PawnPromotion(Position from, Position to, PieceType newType) {
        this.fromPos = from;
        this.toPos = to;
        this.newType = newType;
    }
    
    @Override
    public MoveType getType() {
        return MoveType.PAWN_PROMOTION;
    }
    
    @Override
    public Position getFromPos() {
        return fromPos;
    }
    
    @Override
    public Position getToPos() {
        return toPos;
    }
    
    private Piece createPromotionPiece(Player color) {
        return switch (newType) {
            case KNIGHT -> new Knight(color);
            case BISHOP -> new Bishop(color);
            case ROOK -> new Rook(color);
            default ->  // QUEEN
                    new Queen(color);
        };
    }
    
    @Override
    public boolean execute(Board board) {
        Piece pawn = board.getPiece(fromPos);
        board.setPiece(fromPos, null);
        
        Piece promotionPiece = createPromotionPiece(pawn.getColor());
        promotionPiece.setHasMoved(true);
        board.setPiece(toPos, promotionPiece);
        
        return true;
    }
}