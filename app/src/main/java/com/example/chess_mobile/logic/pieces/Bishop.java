package chess.logic.pieces;

import chess.logic.Board;
import chess.logic.Direction;
import chess.logic.Position;
import chess.logic.moves.Move;
import chess.logic.moves.NormalMove;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece implements Serializable {
    private static final Direction[] DIRECTIONS = {
            Direction.NORTH_EAST,
            Direction.NORTH_WEST,
            Direction.SOUTH_EAST,
            Direction.SOUTH_WEST
    };
    
    private final Player color;
    
    public Bishop(Player color) {
        this.color = color;
    }
    
    @Override
    public PieceType getType() {
        return PieceType.BISHOP;
    }
    
    @Override
    public Player getColor() {
        return color;
    }
    
    @Override
    public Piece copy() {
        Bishop copy = new Bishop(color);
        copy.setHasMoved(getHasMoved());
        return copy;
    }
    
    @Override
    public List<Move> getMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        
        List<Position> positions = getMovePositionsInDirs(from, board, DIRECTIONS);
        for (Position to : positions) {
            moves.add(new NormalMove(from, to));
        }
        
        return moves;
    }
}