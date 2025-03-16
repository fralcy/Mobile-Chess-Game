package com.example.chess_mobile.logic.pieces;

import com.example.chess_mobile.logic.game_states.Player;

import com.example.chess_mobile.logic.game_states.Board;
import com.example.chess_mobile.logic.game_states.Direction;
import com.example.chess_mobile.logic.game_states.Position;
import com.example.chess_mobile.logic.moves.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece implements Serializable {
    private final Player color;
    private final Direction forward;
    
    public Pawn(Player color) {
        this.color = color;
        if (color == Player.WHITE) {
            this.forward = Direction.NORTH;
        } else {
            this.forward = Direction.SOUTH;
        }
    }
    
    @Override
    public PieceType getType() {
        return PieceType.PAWN;
    }
    
    @Override
    public Player getColor() {
        return color;
    }
    
    @Override
    public Piece copy() {
        Pawn copy = new Pawn(color);
        copy.setHasMoved(getHasMoved());
        return copy;
    }
    
    private boolean canMoveTo(Position pos, Board board) {
        return Board.isInside(pos) && board.isEmpty(pos);
    }
    
    private boolean canCaptureAt(Position pos, Board board) {
        if (!Board.isInside(pos) || board.isEmpty(pos)) {
            return false;
        }
        
        return board.getPiece(pos).getColor() != color;
    }
    
    private List<Move> getPromotionMoves(Position from, Position to) {
        List<Move> moves = new ArrayList<>();
        moves.add(new PawnPromotion(from, to, PieceType.KNIGHT));
        moves.add(new PawnPromotion(from, to, PieceType.BISHOP));
        moves.add(new PawnPromotion(from, to, PieceType.ROOK));
        moves.add(new PawnPromotion(from, to, PieceType.QUEEN));
        return moves;
    }
    
    private List<Move> getForwardMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        
        Position oneMovePos = from.plus(forward);
        if (canMoveTo(oneMovePos, board)) {
            if (oneMovePos.getRow() == 0 || oneMovePos.getRow() == 7) {
                // promotion after moving
                moves.addAll(getPromotionMoves(from, oneMovePos));
            } else {
                moves.add(new NormalMove(from, oneMovePos));
            }
            
            // two squares forward if not moved
            if (!getHasMoved()) {
                Position twoMovePos = oneMovePos.plus(forward);
                if (canMoveTo(twoMovePos, board)) {
                    moves.add(new DoublePawn(from, twoMovePos));
                }
            }
        }
        
        return moves;
    }
    
    private List<Move> getDiagonalMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        
        Direction[] dirs = {Direction.WEST, Direction.EAST};
        for (Direction dir : dirs) {
            Position to = from.plus(forward).plus(dir);
            
            if (to.equals(board.getPawnSkipPosition(color.getOpponent()))) {
                moves.add(new EnPassant(from, to));
            } else if (canCaptureAt(to, board)) {
                if (to.getRow() == 0 || to.getRow() == 7) {
                    // promotion after capturing
                    moves.addAll(getPromotionMoves(from, to));
                } else {
                    moves.add(new NormalMove(from, to));
                }
            }
        }
        
        return moves;
    }
    
    @Override
    public List<Move> getMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getForwardMoves(from, board));
        moves.addAll(getDiagonalMoves(from, board));
        return moves;
    }
    
    @Override
    public boolean canCaptureOpponentKing(Position from, Board board) {
        for (Move move : getDiagonalMoves(from, board)) {
            Piece piece = board.getPiece(move.getToPos());
            if (piece != null && piece.getType() == PieceType.KING) {
                return true;
            }
        }
        
        return false;
    }
}