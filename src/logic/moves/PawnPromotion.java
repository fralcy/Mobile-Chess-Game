package chess.logic.moves;

import chess.logic.Board;
import chess.logic.Piece;
import chess.logic.PieceType;
import chess.logic.Player;
import chess.logic.Position;
import chess.logic.pieces.*;

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
        switch (newType) {
            case KNIGHT:
                return new Knight(color);
            case BISHOP:
                return new Bishop(color);
            case ROOK:
                return new Rook(color);
            default:  // QUEEN
                return new Queen(color);
        }
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