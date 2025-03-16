package com.example.chess_mobile.logic.game_states;

import com.example.chess_mobile.logic.pieces.*;
import com.example.chess_mobile.logic.moves.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Board implements Serializable {
    private final Piece[][] pieces = new Piece[8][8];
    private final Map<Player, Position> pawnSkipPositions = new HashMap<>();
    
    public Board() {
        pawnSkipPositions.put(Player.WHITE, null);
        pawnSkipPositions.put(Player.BLACK, null);
    }
    
    public Piece getPiece(int row, int col) {
        return pieces[row][col];
    }
    
    public void setPiece(int row, int col, Piece piece) {
        pieces[row][col] = piece;
    }
    
    public Piece getPiece(Position pos) {
        return getPiece(pos.getRow(), pos.getColumn());
    }
    
    public void setPiece(Position pos, Piece piece) {
        setPiece(pos.getRow(), pos.getColumn(), piece);
    }
    
    public Position getPawnSkipPosition(Player player) {
        return pawnSkipPositions.get(player);
    }
    
    public void setPawnSkipPosition(Player player, Position pos) {
        pawnSkipPositions.put(player, pos);
    }
    
    public Board initial() {
        Board board = new Board();
        board.addStartPieces();
        return board;
    }
    
    private void addStartPieces() {
        // Add black pieces
        this.setPiece(0, 0, new Rook(Player.BLACK));
        this.setPiece(0, 1, new Knight(Player.BLACK));
        this.setPiece(0, 2, new Bishop(Player.BLACK));
        this.setPiece(0, 3, new Queen(Player.BLACK));
        this.setPiece(0, 4, new King(Player.BLACK));
        this.setPiece(0, 5, new Bishop(Player.BLACK));
        this.setPiece(0, 6, new Knight(Player.BLACK));
        this.setPiece(0, 7, new Rook(Player.BLACK));
        
        // Add white pieces
        this.setPiece(7, 0, new Rook(Player.WHITE));
        this.setPiece(7, 1, new Knight(Player.WHITE));
        this.setPiece(7, 2, new Bishop(Player.WHITE));
        this.setPiece(7, 3, new Queen(Player.WHITE));
        this.setPiece(7, 4, new King(Player.WHITE));
        this.setPiece(7, 5, new Bishop(Player.WHITE));
        this.setPiece(7, 6, new Knight(Player.WHITE));
        this.setPiece(7, 7, new Rook(Player.WHITE));
        
        // Add pawns
        for (int c = 0; c < 8; c++) {
            this.setPiece(1, c, new Pawn(Player.BLACK));
            this.setPiece(6, c, new Pawn(Player.WHITE));
        }
    }
    
    public static boolean isInside(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < 8 && pos.getColumn() >= 0 && pos.getColumn() < 8;
    }
    
    public boolean isEmpty(Position pos) {
        return this.getPiece(pos) == null;
    }
    
    public List<Position> getPiecePositions() {
        List<Position> positions = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position pos = new Position(r, c);
                if (!isEmpty(pos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }
    
    public List<Position> getPiecePositionsFor(Player player) {
        return getPiecePositions().stream()
                .filter(pos -> getPiece(pos).getColor() == player)
                .collect(Collectors.toList());
    }
    
    public boolean isInCheck(Player player) {
        return getPiecePositionsFor(player.getOpponent()).stream()
                .anyMatch(pos -> {
                    Piece piece = getPiece(pos);
                    return piece.canCaptureOpponentKing(pos, this);
                });
    }
    
    public Board copy() {
        Board copy = new Board();
        for (Position pos : getPiecePositions()) {
            copy.setPiece(pos, getPiece(pos).copy());
        }
        return copy;
    }
    
    // Castle rights methods
    public boolean canCastleKingSide(Player player) {
        if (player == Player.WHITE) {
            return isUnmovedKingAndRook(new Position(7, 4), new Position(7, 7));
        } else if (player == Player.BLACK) {
            return isUnmovedKingAndRook(new Position(0, 4), new Position(0, 7));
        }
        return false;
    }
    
    public boolean canCastleQueenSide(Player player) {
        if (player == Player.WHITE) {
            return isUnmovedKingAndRook(new Position(7, 4), new Position(7, 0));
        } else if (player == Player.BLACK) {
            return isUnmovedKingAndRook(new Position(0, 4), new Position(0, 0));
        }
        return false;
    }
    
    private boolean isUnmovedKingAndRook(Position kingPos, Position rookPos) {
        if (isEmpty(kingPos) || isEmpty(rookPos)) {
            return false;
        }
        
        Piece king = getPiece(kingPos);
        Piece rook = getPiece(rookPos);
        
        return king.getType() == PieceType.KING && rook.getType() == PieceType.ROOK
                && !king.getHasMoved() && !rook.getHasMoved();
    }
    
    // En passant methods
    public boolean canCaptureEnPassant(Player player) {
        Position skipPos = getPawnSkipPosition(player.getOpponent());
        
        if (skipPos == null) {
            return false;
        }
        
        Position[] pawnPositions;
        if (player == Player.WHITE) {
            pawnPositions = new Position[] {
                skipPos.plus(Direction.SOUTH_WEST),
                skipPos.plus(Direction.SOUTH_EAST)
            };
        } else {
            pawnPositions = new Position[] {
                skipPos.plus(Direction.NORTH_WEST),
                skipPos.plus(Direction.NORTH_EAST)
            };
        }
        
        return hasPawnInPosition(player, pawnPositions, skipPos);
    }
    
    private boolean hasPawnInPosition(Player player, Position[] pawnPositions, Position skipPos) {
        for (Position pos : pawnPositions) {
            if (!isInside(pos)) continue;
            
            Piece piece = getPiece(pos);
            if (piece == null || piece.getColor() != player || piece.getType() != PieceType.PAWN) {
                continue;
            }
            
            EnPassant move = new EnPassant(pos, skipPos);
            if (move.isLegal(this)) {
                return true;
            }
        }
        
        return false;
    }
    // Counting methods
    public Counting countPieces() {
        Counting counting = new Counting();
        for (Position pos : getPiecePositions()) {
            Piece piece = getPiece(pos);
            counting.increment(piece.getColor(), piece.getType());
        }
        return counting;
    }
    
    public boolean hasInsufficientMaterial() {
        Counting counting = countPieces();
        return isKingVKing(counting) || 
               isKingBishopVKing(counting) || 
               isKingKnightVKing(counting) || 
               isKingBishopVKingBishop(counting);
    }
    
    private boolean isKingVKing(Counting counting) {
        return counting.getTotalCount() == 2;
    }
    
    private boolean isKingBishopVKing(Counting counting) {
        return counting.getTotalCount() == 3 && 
              (counting.getWhite(PieceType.BISHOP) == 1 || 
               counting.getBlack(PieceType.BISHOP) == 1);
    }
    
    private boolean isKingKnightVKing(Counting counting) {
        return counting.getTotalCount() == 3 && 
              (counting.getWhite(PieceType.KNIGHT) == 1 || 
               counting.getBlack(PieceType.KNIGHT) == 1);
    }
    
    private boolean isKingBishopVKingBishop(Counting counting) {
        if (counting.getTotalCount() != 4) {
            return false;
        }
        
        if (counting.getWhite(PieceType.BISHOP) != 1 || 
            counting.getBlack(PieceType.BISHOP) != 1) {
            return false;
        }
        
        Position wBishopPos = findPiece(Player.WHITE);
        Position bBishopPos = findPiece(Player.BLACK);

        assert bBishopPos != null;
        assert wBishopPos != null;
        return wBishopPos.getSquareColor() == bBishopPos.getSquareColor();
    }
    
    private Position findPiece(Player color) {
        for (Position pos : getPiecePositionsFor(color)) {
            if (getPiece(pos).getType() == PieceType.BISHOP) {
                return pos;
            }
        }
        
        return null; // Should never happen
    }
}