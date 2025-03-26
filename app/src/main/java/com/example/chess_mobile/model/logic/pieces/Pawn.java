package com.example.chess_mobile.model.logic.pieces;

import com.example.chess_mobile.model.logic.game_states.EPlayer;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.Direction;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.DoublePawn;
import com.example.chess_mobile.model.logic.moves.EnPassant;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.moves.NormalMove;
import com.example.chess_mobile.model.logic.moves.PawnPromotion;
import com.example.chess_mobile.model.moves.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece implements Serializable {
    private final EPlayer color;
    private final Direction forward;
    
    public Pawn(EPlayer color) {
        this.color = color;
        if (color == EPlayer.WHITE) {
            this.forward = Direction.NORTH;
        } else {
            this.forward = Direction.SOUTH;
        }
    }
    
    @Override
    public EPieceType getType() {
        return EPieceType.PAWN;
    }
    
    @Override
    public EPlayer getPlayerColor() {
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
        
        return board.getPiece(pos).getPlayerColor() != color;
    }
    
    private List<Move> getPromotionMoves(Position from, Position to) {
        List<Move> moves = new ArrayList<>();
        moves.add(new PawnPromotion(from, to, EPieceType.KNIGHT));
        moves.add(new PawnPromotion(from, to, EPieceType.BISHOP));
        moves.add(new PawnPromotion(from, to, EPieceType.ROOK));
        moves.add(new PawnPromotion(from, to, EPieceType.QUEEN));
        return moves;
    }
    
    private List<Move> getForwardMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        
        Position oneMovePos = from.plus(forward);
        if (canMoveTo(oneMovePos, board)) {
            if (oneMovePos.row() == 0 || oneMovePos.row() == 7) {
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
                if (to.row() == 0 || to.row() == 7) {
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
            if (piece != null && piece.getType() == EPieceType.KING) {
                return true;
            }
        }
        
        return false;
    }
}