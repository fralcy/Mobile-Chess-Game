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

public class Knight extends Piece implements Serializable {
    private final EPlayer color;
    
    public Knight(EPlayer color) {
        this.color = color;
    }
    
    @Override
    public EPieceType getType() {
        return EPieceType.KNIGHT;
    }
    
    @Override
    public EPlayer getPlayerColor() {
        return color;
    }
    
    @Override
    public Piece copy() {
        Knight copy = new Knight(color);
        copy.setHasMoved(getHasMoved());
        return copy;
    }
    
    private List<Position> getPotentialPositions(Position from) {
        List<Position> positions = new ArrayList<>();
        
        Direction[] vDirections = {Direction.NORTH, Direction.SOUTH};
        Direction[] hDirections = {Direction.WEST, Direction.EAST};
        
        for (Direction vDir : vDirections) {
            for (Direction hDir : hDirections) {
                // L-shape
                positions.add(from.plus(vDir.multiply(2)).plus(hDir));
                positions.add(from.plus(hDir.multiply(2)).plus(vDir));
            }
        }
        
        return positions;
    }
    
    private List<Position> getValidPositions(Position from, Board board) {
        List<Position> validPositions = new ArrayList<>();
        
        for (Position pos : getPotentialPositions(from)) {
            if (Board.isInside(pos) && 
                (board.isEmpty(pos) || board.getPiece(pos).getPlayerColor() != color)) {
                validPositions.add(pos);
            }
        }
        
        return validPositions;
    }
    
    @Override
    public List<Move> getMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        
        for (Position to : getValidPositions(from, board)) {
            moves.add(new NormalMove(from, to));
        }
        
        return moves;
    }
}