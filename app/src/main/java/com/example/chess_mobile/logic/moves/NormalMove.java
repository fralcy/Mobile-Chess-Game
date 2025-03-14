package chess.logic.moves;

import chess.logic.Board;
import chess.logic.Piece;
import chess.logic.PieceType;
import chess.logic.Position;

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