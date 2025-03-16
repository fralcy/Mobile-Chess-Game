package com.example.chess_mobile.logic.pieces;

import com.example.chess_mobile.logic.game_states.Player;

import com.example.chess_mobile.logic.game_states.Board;
import com.example.chess_mobile.logic.game_states.Direction;
import com.example.chess_mobile.logic.game_states.Position;
import com.example.chess_mobile.logic.moves.Move;
import com.example.chess_mobile.logic.moves.NormalMove;

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
    
    private final Player color;
    
    public Rook(Player color) {
        this.color = color;
    }
    
    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }
    
    @Override
    public Player getColor() {
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