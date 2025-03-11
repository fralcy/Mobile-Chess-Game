package chess.logic.pieces;

import chess.logic.Board;
import chess.logic.Direction;
import chess.logic.Position;
import chess.logic.moves.Castle;
import chess.logic.moves.Move;
import chess.logic.moves.MoveType;
import chess.logic.moves.NormalMove;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece implements Serializable {
    private static final Direction[] DIRECTIONS = {
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST,
            Direction.NORTH_EAST,
            Direction.NORTH_WEST,
            Direction.SOUTH_EAST,
            Direction.SOUTH_WEST
    };
    
    private final Player color;
    
    public King(Player color) {
        this.color = color;
    }
    
    @Override
    public PieceType getType() {
        return PieceType.KING;
    }
    
    @Override
    public Player getColor() {
        return color;
    }
    
    @Override
    public Piece copy() {
        King copy = new King(color);
        copy.setHasMoved(getHasMoved());
        return copy;
    }
    
    private List<Position> getMovePositions(Position from, Board board) {
        List<Position> positions = new ArrayList<>();
        
        for (Direction dir : DIRECTIONS) {
            Position to = from.plus(dir);
            if (Board.isInside(to) && 
                (board.isEmpty(to) || board.getPiece(to).getColor() != color)) {
                positions.add(to);
            }
        }
        
        return positions;
    }
    
    private boolean canCastleKingSide(Position from, Board board) {
        if (getHasMoved()) {
            return false;
        }
        
        Position rookPos = new Position(from.getRow(), 7);
        Position[] betweenPositions = {
            new Position(from.getRow(), 5),
            new Position(from.getRow(), 6)
        };
        
        return isUnmovedRook(rookPos, board) && allEmpty(betweenPositions, board);
    }
    
    private boolean canCastleQueenSide(Position from, Board board) {
        if (getHasMoved()) {
            return false;
        }
        
        Position rookPos = new Position(from.getRow(), 0);
        Position[] betweenPositions = {
            new Position(from.getRow(), 1),
            new Position(from.getRow(), 2),
            new Position(from.getRow(), 3)
        };
        
        return isUnmovedRook(rookPos, board) && allEmpty(betweenPositions, board);
    }
    
    private boolean isUnmovedRook(Position pos, Board board) {
        if (board.isEmpty(pos)) {
            return false;
        }
        
        Piece piece = board.getPiece(pos);
        return piece.getType() == PieceType.ROOK && !piece.getHasMoved();
    }
    
    private boolean allEmpty(Position[] positions, Board board) {
        for (Position pos : positions) {
            if (!board.isEmpty(pos)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public List<Move> getMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        
        // Normal moves
        for (Position to : getMovePositions(from, board)) {
            moves.add(new NormalMove(from, to));
        }
        
        // Castling
        if (canCastleKingSide(from, board)) {
            moves.add(new Castle(MoveType.CASTLE_KS, from));
        }
        
        if (canCastleQueenSide(from, board)) {
            moves.add(new Castle(MoveType.CASTLE_QS, from));
        }
        
        return moves;
    }
    
    @Override
    public boolean canCaptureOpponentKing(Position from, Board board) {
        for (Position to : getMovePositions(from, board)) {
            Piece piece = board.getPiece(to);
            if (piece != null && piece.getType() == PieceType.KING) {
                return true;
            }
        }
        
        return false;
    }
}