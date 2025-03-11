package chess.logic.moves;

import chess.logic.Board;
import chess.logic.Direction;
import chess.logic.Player;
import chess.logic.Position;

public class Castle extends Move {
    private final MoveType type;
    private final Position fromPos;
    private final Position toPos;
    private final Direction kingMoveDir;
    private final Position rookFromPos;
    private final Position rookToPos;
    
    public Castle(MoveType type, Position kingPos) {
        this.type = type;
        this.fromPos = kingPos;
        
        if (type == MoveType.CASTLE_KS) {
            kingMoveDir = Direction.EAST;
            toPos = new Position(kingPos.getRow(), 6);
            rookFromPos = new Position(kingPos.getRow(), 7);
            rookToPos = new Position(kingPos.getRow(), 5);
        } else {  // CASTLE_QS
            kingMoveDir = Direction.WEST;
            toPos = new Position(kingPos.getRow(), 2);
            rookFromPos = new Position(kingPos.getRow(), 0);
            rookToPos = new Position(kingPos.getRow(), 3);
        }
    }
    
    @Override
    public MoveType getType() {
        return type;
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
        new NormalMove(rookFromPos, rookToPos).execute(board);
        
        return false;
    }
    
    @Override
    public boolean isLegal(Board board) {
        Player player = board.getPiece(fromPos).getColor();
        
        if (board.isInCheck(player)) {
            return false;
        }
        
        Board copy = board.copy();
        Position kingPosInCopy = fromPos;
        
        for (int i = 0; i < 2; i++) {
            Position nextPos = new Position(kingPosInCopy.getRow(), 
                                         kingPosInCopy.getColumn() + kingMoveDir.getColumnDelta());
            new NormalMove(kingPosInCopy, nextPos).execute(copy);
            kingPosInCopy = nextPos;
            
            if (copy.isInCheck(player)) {
                return false;
            }
        }
        
        return true;
    }
}