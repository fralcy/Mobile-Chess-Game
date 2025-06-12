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
    private EPlayer _aiColor = EPlayer.BLACK; // AI color, mặc định là đen

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

    // Getters
    public int getDifficulty() { return this._difficulty; }

    @Override
    public boolean isThinking() { return this._isThinking; }

    @Override
    public void setDifficulty(int level) {
        this._difficulty = Math.max(1, Math.min(5, level));
    }

    // Setter cho màu quân của AI
    public void setAIColor(EPlayer color) {
        this._aiColor = color;
    }

    // Trả về màu quân người chơi (ngược với AI)
    private EPlayer getOpponentColor() {
        return _aiColor == EPlayer.BLACK ? EPlayer.WHITE : EPlayer.BLACK;
    }

    @Override
    public Move calculateMove(Board board, int difficulty) {
        this._difficulty = difficulty;
        this._isThinking = true;

        Move bestMove = switch (difficulty) {
            case 2 -> calculateBasicMove(board);
            case 3 -> calculateNormalMove(board);
            case 4 -> calculateHardMove(board);
            case 5 -> calculateExpertMove(board);
            default -> calculateRandomMove(board);
        };

        this._isThinking = false;
        return bestMove;
    }

    // ------------------ CÁC LEVEL ------------------

    private Move calculateRandomMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, _aiColor);
        return allMoves.isEmpty() ? getRandomMove(board) : allMoves.get(_random.nextInt(allMoves.size()));
    }

    private Move calculateBasicMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, _aiColor);
        if (allMoves.isEmpty()) return getRandomMove(board);

        // Tìm nước ăn quân trước
        for (Move move : allMoves) {
            if (!board.isEmpty(move.getToPos())) {
                Piece target = board.getPiece(move.getToPos());
                if (target.getPlayerColor() == getOpponentColor()) return move;
            }
        }
        return allMoves.get(_random.nextInt(allMoves.size()));
    }

    private Move calculateNormalMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, _aiColor);
        if (allMoves.isEmpty()) return getRandomMove(board);

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

    private Move calculateHardMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, _aiColor);
        if (allMoves.isEmpty()) return getRandomMove(board);

        Move bestMove = allMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Move move : allMoves) {
            Board temp = board.copy();
            move.execute(temp);
            int score = evaluateBoard(temp) - getBestOpponentResponse(temp);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private Move calculateExpertMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, _aiColor);
        if (allMoves.isEmpty()) return getRandomMove(board);

        Move bestMove = allMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Move move : allMoves) {
            Board temp = board.copy();
            move.execute(temp);
            int score = minimax(temp, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean isMax) {
        if (depth == 0) return evaluateBoard(board);

        EPlayer current = isMax ? _aiColor : getOpponentColor();
        List<Move> moves = getAllPossibleMoves(board, current);
        if (moves.isEmpty()) return evaluateBoard(board);

        if (isMax) {
            int max = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board temp = board.copy();
                move.execute(temp);
                int score = minimax(temp, depth - 1, alpha, beta, false);
                max = Math.max(max, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board temp = board.copy();
                move.execute(temp);
                int score = minimax(temp, depth - 1, alpha, beta, true);
                min = Math.min(min, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return min;
        }
    }

    // ------------------ EVALUATION ------------------

    private int evaluateMove(Board board, Move move) {
        int score = 0;

        // Điểm ăn quân
        if (!board.isEmpty(move.getToPos())) {
            Piece captured = board.getPiece(move.getToPos());
            if (captured.getPlayerColor() == getOpponentColor()) {
                score += PIECE_VALUES.get(captured.getType());
            }
        }

        // Điểm kiểm soát trung tâm
        Position[] centerSquares = {
                new Position(3, 3), new Position(3, 4),
                new Position(4, 3), new Position(4, 4)
        };
        for (Position center : centerSquares) {
            if (move.getToPos().equals(center)) score += 50;
        }

        return score;
    }

    private int evaluateBoard(Board board) {
        int score = 0;
        for (Position pos : board.getPiecePositions()) {
            Piece piece = board.getPiece(pos);
            int val = PIECE_VALUES.get(piece.getType());
            // Cộng điểm cho AI, trừ điểm cho đối thủ
            score += piece.getPlayerColor() == _aiColor ? val : -val;
        }
        return score;
    }

    private int getBestOpponentResponse(Board board) {
        List<Move> moves = getAllPossibleMoves(board, getOpponentColor());
        if (moves.isEmpty()) return 0;

        int best = Integer.MIN_VALUE;
        for (Move move : moves) {
            int score = evaluateMove(board, move);
            best = Math.max(best, score);
        }
        return best;
    }

    private List<Move> getAllPossibleMoves(Board board, EPlayer player) {
        return board.getPiecePositionsFor(player).stream()
                .flatMap(pos -> board.getPiece(pos).getMoves(pos, board).stream())
                .filter(move -> move.isLegal(board))
                .collect(Collectors.toList());
    }

    @Override
    public Move getRandomMove(Board board) {
        List<Move> allMoves = getAllPossibleMoves(board, _aiColor);
        return allMoves.isEmpty() ? null : allMoves.get(_random.nextInt(allMoves.size()));
    }
}