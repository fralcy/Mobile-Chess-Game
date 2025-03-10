package chess.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Piece implements Serializable {
    protected PositionValue ps = new PositionValue();
    protected boolean hasMoved = false;
    
    public abstract double getWeight();
    public abstract double[][] getPosVal();
    public abstract PieceType getType();
    public abstract Player getColor();
    public abstract Piece copy();
    public abstract List<Move> getMoves(Position from, Board board);
    
    public boolean getHasMoved() {
        return hasMoved;
    }
    
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    
    protected List<Position> getMovePositionsInDir(Position from, Board board, Direction dir) {
        List<Position> positions = new ArrayList<>();
        Position pos = from.plus(dir);
        
        while (Board.isInside(pos)) {
            if (board.isEmpty(pos)) {
                positions.add(pos);
                pos = pos.plus(dir);
                continue;
            }
            
            Piece piece = board.getPiece(pos);
            if (piece.getColor() != this.getColor()) {
                positions.add(pos);
            }
            
            break;
        }
        
        return positions;
    }
    
    protected List<Position> getMovePositionsInDirs(Position from, Board board, Direction[] dirs) {
        List<Position> positions = new ArrayList<>();
        for (Direction dir : dirs) {
            positions.addAll(getMovePositionsInDir(from, board, dir));
        }
        return positions;
    }
    
    public boolean canCaptureOpponentKing(Position from, Board board) {
        return getMoves(from, board).stream()
                .anyMatch(move -> {
                    Piece piece = board.getPiece(move.getToPos());
                    return piece != null && piece.getType() == PieceType.KING;
                });
    }
}