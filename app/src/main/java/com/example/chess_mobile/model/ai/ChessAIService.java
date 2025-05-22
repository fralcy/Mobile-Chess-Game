package com.example.chess_mobile.model.ai;

import com.example.chess_mobile.model.interfaces.IAIService;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.Piece;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ChessAIService implements IAIService {
    private int _difficulty = 1;
    private boolean _isThinking = false;
    private final Random _random = new Random();

    // Piece values for evaluation
    private static final Map<EPieceType, Integer> PIECE_VALUES = new HashMap<>();
    static {
        PIECE_VALUES.put(EPieceType.PAWN, 100);
        PIECE_VALUES.put(EPieceType.KNIGHT, 320);
        PIECE_VALUES.put(EPieceType.BISHOP, 330);
        PIECE_VALUES.put(EPieceType.ROOK, 500);
        PIECE_VALUES.put(EPieceType.QUEEN, 900);
        PIECE_VALUES.put(EPieceType.KING, 20000);
    }

    @Override
    public Move calculateMove(Board board, int difficulty) {
        this._difficulty = difficulty;
        this._isThinking = true;

        Move bestMove = switch (difficulty) {
            case 1 -> calculateRandomMove(board);
            case 2 -> calculateBasicMove(board);
            case 3 -> calculateNormalMove(board);
            case 4 -> calculateHardMove(board);
            case 5 -> calculateExpertMove(board);
            default -> calculateRandomMove(board);
        };

        this._isThinking = false;
        return bestMove;
    }

    @Override
    public void setDifficulty(int level) {
        this._difficulty = Math.max(1, Math.min(5, level));
    }

    @Override
    public boolean isThinking() {
        return this._isThinking;
    }

    // Level 1: Random moves
    private Move calculateRandomMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, EPlayer.BLACK);
        if (allMoves.isEmpty()) {
            return getRandomMove(board);
        }
        return allMoves.get(_random.nextInt(allMoves.size()));
    }

    // Level 2: Basic logic (capture pieces, avoid losing pieces)
    private Move calculateBasicMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, EPlayer.BLACK);
        if (allMoves.isEmpty()) {
            return getRandomMove(board);
        }

        // Try to find capturing moves first
        for (Move move : allMoves) {
            if (!board.isEmpty(move.getToPos())) {
                Piece capturedPiece = board.getPiece(move.getToPos());
                if (capturedPiece.getPlayerColor() == EPlayer.WHITE) {
                    return move;
                }
            }
        }

        // If no captures, make random move
        return allMoves.get(_random.nextInt(allMoves.size()));
    }

    // Level 3: Normal logic (material evaluation)
    private Move calculateNormalMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, EPlayer.BLACK);
        if (allMoves.isEmpty()) {
            return getRandomMove(board);
        }

        Move bestMove = allMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Move move : allMoves) {
            int score = evaluateMove(board, move);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Level 4: Hard logic (look ahead 1-2 moves)
    private Move calculateHardMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, EPlayer.BLACK);
        if (allMoves.isEmpty()) {
            return getRandomMove(board);
        }

        Move bestMove = allMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Move move : allMoves) {
            Board tempBoard = board.copy();
            move.execute(tempBoard);

            // Look ahead one move for opponent
            int score = evaluateBoard(tempBoard) - getBestOpponentResponse(tempBoard);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Level 5: Expert logic (minimax with alpha-beta pruning)
    private Move calculateExpertMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, EPlayer.BLACK);
        if (allMoves.isEmpty()) {
            return getRandomMove(board);
        }

        Move bestMove = allMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Move move : allMoves) {
            Board tempBoard = board.copy();
            move.execute(tempBoard);

            // Use minimax with depth 2
            int score = minimax(tempBoard, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Minimax algorithm with alpha-beta pruning
    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0) {
            return evaluateBoard(board);
        }

        EPlayer player = isMaximizing ? EPlayer.BLACK : EPlayer.WHITE;
        List<Move> moves = getAllPossibleMoves(board, player);

        if (moves.isEmpty()) {
            return evaluateBoard(board);
        }

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board tempBoard = board.copy();
                move.execute(tempBoard);
                int score = minimax(tempBoard, depth - 1, alpha, beta, false);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board tempBoard = board.copy();
                move.execute(tempBoard);
                int score = minimax(tempBoard, depth - 1, alpha, beta, true);
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return minScore;
        }
    }

    // Get all possible moves for a player
    private List<Move> getAllPossibleMoves(Board board, EPlayer player) {
        return board.getPiecePositionsFor(player).stream()
                .flatMap(pos -> board.getPiece(pos).getMoves(pos, board).stream())
                .filter(move -> move.isLegal(board))
                .collect(Collectors.toList());
    }

    // Evaluate a single move
    private int evaluateMove(Board board, Move move) {
        int score = 0;

        // Capture value
        if (!board.isEmpty(move.getToPos())) {
            Piece capturedPiece = board.getPiece(move.getToPos());
            if (capturedPiece.getPlayerColor() == EPlayer.WHITE) {
                score += PIECE_VALUES.get(capturedPiece.getType());
            }
        }

        // Center control
        Position center1 = new Position(3, 3);
        Position center2 = new Position(3, 4);
        Position center3 = new Position(4, 3);
        Position center4 = new Position(4, 4);

        if (move.getToPos().equals(center1) || move.getToPos().equals(center2) ||
                move.getToPos().equals(center3) || move.getToPos().equals(center4)) {
            score += 50;
        }

        return score;
    }

    // Evaluate board position
    private int evaluateBoard(Board board) {
        int score = 0;

        for (Position pos : board.getPiecePositions()) {
            Piece piece = board.getPiece(pos);
            int pieceValue = PIECE_VALUES.get(piece.getType());

            if (piece.getPlayerColor() == EPlayer.BLACK) {
                score += pieceValue;
            } else {
                score -= pieceValue;
            }
        }

        return score;
    }

    // Get best opponent response (simplified)
    private int getBestOpponentResponse(Board board) {
        List<Move> opponentMoves = getAllPossibleMoves(board, EPlayer.WHITE);
        if (opponentMoves.isEmpty()) {
            return 0;
        }

        int bestOpponentScore = Integer.MIN_VALUE;
        for (Move move : opponentMoves) {
            int score = evaluateMove(board, move);
            bestOpponentScore = Math.max(bestOpponentScore, score);
        }

        return bestOpponentScore;
    }
}