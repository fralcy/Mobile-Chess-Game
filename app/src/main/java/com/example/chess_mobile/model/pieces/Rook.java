package com.example.chess_mobile.model.pieces;

import com.example.chess_mobile.model.game_states.EPlayer;

import com.example.chess_mobile.model.game_states.Board;
import com.example.chess_mobile.model.game_states.Direction;
import com.example.chess_mobile.model.game_states.Position;
import com.example.chess_mobile.model.moves.Move;
import com.example.chess_mobile.model.moves.NormalMove;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece implements Serializable {
    private static final Direction[] DIRECTIONS = {
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    };
    
    private final EPlayer color;
    
    public Rook(EPlayer color) {
        this.color = color;
    }
    
    @Override
    public EPieceType getType() {
        return EPieceType.ROOK;
    }
    
    @Override
    public EPlayer getPlayerColor() {
        return color;
    }
    
    @Override
    public Piece copy() {
        Rook copy = new Rook(color);
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