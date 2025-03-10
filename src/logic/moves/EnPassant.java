package chess.logic.moves;

import chess.logic.Board;
import chess.logic.Position;

public class EnPassant extends Move {
    private final Position fromPos;
    private final Position toPos;
    private final Position capturePos;
    
    public EnPassant(Position from, Position to) {
        this.fromPos = from;
        this.toPos = to;
        this.capturePos = new Position(from.getRow(), to.getColumn());
    }
    
    @Override
    public MoveType getType() {
        return MoveType.EN_PASSANT;
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