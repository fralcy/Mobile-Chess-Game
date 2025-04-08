package com.example.chess_mobile.model.ai;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.Piece;

/**
 * Evaluates the board position and returns a score from the perspective of the given player
 */
public class BoardEvaluator {
    // Piece values (standard chess piece values in centipawns)
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000; // King is extremely valuable

    // Piece-Square tables for positional evaluation
    // These tables encourage pieces to move to good squares
    // Values are from white's perspective - will be flipped for black

    // Pawns are encouraged to advance and control the center
    private static final int[][] PAWN_TABLE = {
            {0,  0,  0,  0,  0,  0,  0,  0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5,  5, 10, 25, 25, 10,  5,  5},
            {0,  0,  0, 20, 20,  0,  0,  0},
            {5, -5,-10,  0,  0,-10, -5,  5},
            {5, 10, 10,-20,-20, 10, 10,  5},
            {0,  0,  0,  0,  0,  0,  0,  0}
    };

    // Knights are encouraged to stay near the center and avoid the edges
    private static final int[][] KNIGHT_TABLE = {
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  0, 15, 20, 20, 15,  0,-30},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  5,  5,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };

    // Bishops are encouraged to control diagonals and stay away from corners
    private static final int[][] BISHOP_TABLE = {
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  5,  5,  5,  5,  5,  5,-10},
            {-10,  0,  5,  0,  0,  5,  0,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20}
    };

    // Rooks are encouraged to control open files and 7th rank
    private static final int[][] ROOK_TABLE = {
            {0,  0,  0,  0,  0,  0,  0,  0},
            {5, 10, 10, 10, 10, 10, 10,  5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {0,  0,  0,  5,  5,  0,  0,  0}
    };

    // Queens combine the power of rooks and bishops
    private static final int[][] QUEEN_TABLE = {
            {-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            {-5,  0,  5,  5,  5,  5,  0, -5},
            {0,  0,  5,  5,  5,  5,  0, -5},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20}
    };

    // Kings are encouraged to stay protected in the corners in the mid game
    private static final int[][] KING_TABLE_MID_GAME = {
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {20, 20,  0,  0,  0,  0, 20, 20},
            {20, 30, 10,  0,  0, 10, 30, 20}
    };

    // Kings are encouraged to move to the center in the endgame
    private static final int[][] KING_TABLE_ENDGAME = {
            {-50,-40,-30,-20,-20,-30,-40,-50},
            {-30,-20,-10,  0,  0,-10,-20,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-30,  0,  0,  0,  0,-30,-30},
            {-50,-30,-30,-30,-30,-30,-30,-50}
    };

    /**
     * Get the position value from the appropriate piece-square table
     *
     * @param pieceType Type of piece
     * @param position Position on board
     * @param board The game board
     * @param isEndgame Whether the game is in endgame stage
     * @return Position value from the piece-square table
     */
    private static int getPieceSquareValue(EPieceType pieceType, Position position, Board board, boolean isEndgame) {
        int row = position.row();
        int col = position.column();

        // Reverse row index for black pieces
        if (board.getPiece(position) != null && board.getPiece(position).getPlayerColor() == EPlayer.BLACK) {
            row = 7 - row;
        }

        switch (pieceType) {
            case PAWN:
                return PAWN_TABLE[row][col];
            case KNIGHT:
                return KNIGHT_TABLE[row][col];
            case BISHOP:
                return BISHOP_TABLE[row][col];
            case ROOK:
                return ROOK_TABLE[row][col];
            case QUEEN:
                return QUEEN_TABLE[row][col];
            case KING:
                return isEndgame ? KING_TABLE_ENDGAME[row][col] : KING_TABLE_MID_GAME[row][col];
            default:
                return 0;
        }
    }

    /**
     * Get the material value of a piece
     *
     * @param pieceType Type of piece
     * @return Piece value in centipawns
     */
    private static int getPieceValue(EPieceType pieceType) {
        switch (pieceType) {
            case PAWN:
                return PAWN_VALUE;
            case KNIGHT:
                return KNIGHT_VALUE;
            case BISHOP:
                return BISHOP_VALUE;
            case ROOK:
                return ROOK_VALUE;
            case QUEEN:
                return QUEEN_VALUE;
            case KING:
                return KING_VALUE;
            default:
                return 0;
        }
    }

    /**
     * Count the total material value for a player
     *
     * @param board Game board
     * @param player Player to evaluate for
     * @return Total material value
     */
    private static int countMaterial(Board board, EPlayer player) {
        int total = 0;
        for (Position pos : board.getPiecePositionsFor(player)) {
            Piece piece = board.getPiece(pos);
            total += getPieceValue(piece.getType());
        }
        return total;
    }

    /**
     * Determine if the game is in endgame based on material
     *
     * @param board Game board
     * @return True if in endgame, false otherwise
     */
    private static boolean isEndgame(Board board) {
        // Check if queen is missing for either side, or
        // if total material (except kings) is less than rook + bishop
        int whiteQueens = 0;
        int blackQueens = 0;
        int whiteMaterial = 0;
        int blackMaterial = 0;

        for (Position pos : board.getPiecePositions()) {
            Piece piece = board.getPiece(pos);
            if (piece.getType() == EPieceType.KING) {
                continue; // Don't count kings in material assessment
            }

            int value = getPieceValue(piece.getType());

            if (piece.getType() == EPieceType.QUEEN) {
                if (piece.getPlayerColor() == EPlayer.WHITE) {
                    whiteQueens++;
                } else {
                    blackQueens++;
                }
            }

            if (piece.getPlayerColor() == EPlayer.WHITE) {
                whiteMaterial += value;
            } else {
                blackMaterial += value;
            }
        }

        // If either side is missing a queen or has low material, consider it endgame
        return (whiteQueens == 0 || blackQueens == 0 ||
                whiteMaterial < ROOK_VALUE + BISHOP_VALUE ||
                blackMaterial < ROOK_VALUE + BISHOP_VALUE);
    }

    /**
     * Evaluate board from the perspective of the given player
     *
     * @param board The game board
     * @param player Player to evaluate for
     * @return Evaluation score (positive is good for player)
     */
    public static int evaluate(Board board, EPlayer player) {
        EPlayer opponent = player.getOpponent();
        boolean endgame = isEndgame(board);

        // Material difference
        int playerMaterial = countMaterial(board, player);
        int opponentMaterial = countMaterial(board, opponent);
        int materialScore = playerMaterial - opponentMaterial;

        // Positional score based on piece-square tables
        int positionalScore = 0;
        for (Position pos : board.getPiecePositions()) {
            Piece piece = board.getPiece(pos);
            int posValue = getPieceSquareValue(piece.getType(), pos, board, endgame);

            if (piece.getPlayerColor() == player) {
                positionalScore += posValue;
            } else {
                positionalScore -= posValue;
            }
        }

        // Check for checkmate and stalemate
        if (board.isInCheck(player)) {
            materialScore -= 500; // Big penalty for being in check
        }

        if (board.isInCheck(opponent)) {
            materialScore += 500; // Big bonus for putting opponent in check
        }

        // Return total score
        return materialScore + (positionalScore / 10); // Weight positional score less
    }
}