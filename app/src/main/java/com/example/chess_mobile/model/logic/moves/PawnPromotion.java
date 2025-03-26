package com.example.chess_mobile.model.logic.moves;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.pieces.Bishop;
import com.example.chess_mobile.model.logic.pieces.Knight;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.pieces.Queen;
import com.example.chess_mobile.model.logic.pieces.Rook;

public class PawnPromotion extends Move {
    private final Position fromPos;
    private final Position toPos;
    private final EPieceType newType;
    
    public PawnPromotion(Position from, Position to, EPieceType newType) {
        this.fromPos = from;
        this.toPos = to;
        this.newType = newType;
    }
    
    @Override
    public EMoveType getType() {
        return EMoveType.PAWN_PROMOTION;
    }
    
    @Override
    public Position getFromPos() {
        return fromPos;
    }
    
    @Override
    public Position getToPos() {
        return toPos;
    }
    
    private Piece createPromotionPiece(EPlayer color) {
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
        
        Piece promotionPiece = createPromotionPiece(pawn.getPlayerColor());
        promotionPiece.setHasMoved(true);
        board.setPiece(toPos, promotionPiece);
        
        return true;
    }
}