package com.example.chess_mobile.model.logic.pieces;

import com.example.chess_mobile.model.logic.game_states.EPlayer;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.Direction;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.moves.NormalMove;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece implements Serializable {
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
    
    private final EPlayer color;
    
    public Queen(EPlayer color) {
        this.color = color;
    }
    
    @Override
    public EPieceType getType() {
        return EPieceType.QUEEN;
    }
    
    @Override
    public EPlayer getPlayerColor() {
        return color;
    }
    
    @Override
    public Piece copy() {
        Queen copy = new Queen(color);
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