package com.example.chess_mobile.model.pieces;

import com.example.chess_mobile.model.game_states.Board;
import com.example.chess_mobile.model.game_states.Direction;
import com.example.chess_mobile.model.game_states.EPlayer;

import com.example.chess_mobile.model.game_states.Position;
import com.example.chess_mobile.model.moves.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements Serializable {
    protected boolean hasMoved = false;
    
    public abstract EPieceType getType();
    public abstract EPlayer getPlayerColor();
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
            if (piece.getPlayerColor() != this.getPlayerColor()) {
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
        for (Move move : getMoves(from, board)) {
            Piece piece = board.getPiece(move.getToPos());
            if (piece != null && piece.getType() == EPieceType.KING) {
                return true;
            }
        }
        return false;
    }
}