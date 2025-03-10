package chess.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Board implements Serializable {
    private final Piece[][] pieces = new Piece[8][8];
    private double boardValue = 0;
    private final Map<Player, Position> pawnSkipPositions = new HashMap<>();
    
    public Board() {
        pawnSkipPositions.put(Player.WHITE, null);
        pawnSkipPositions.put(Player.BLACK, null);
    }
    
    public void setValue() {
        boardValue = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    boardValue += pieces[i][j].getWeight() + pieces[i][j].getPosVal()[7 - i][j];
                }
            }
        }
    }
    
    public double getBoardValue() {
        return boardValue;
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
    
    // Space for more methods
    
}