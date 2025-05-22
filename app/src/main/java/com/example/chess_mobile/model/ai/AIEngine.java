package com.example.chess_mobile.model.ai;

import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.EPieceType;

import java.util.List;
import java.util.Random;

/**
 * Main AI player class that uses the Minimax algorithm to choose moves
 */
public class AIEngine implements IAIEngine {
    private final EPlayer _playerColor;
    private int _difficulty;
    private final Random _random = new Random();

    /**
     * Initialize the AI player
     *
     * @param playerColor Which color the AI plays
     * @param difficulty Level 1-5, higher is stronger
     */
    public AIEngine(EPlayer playerColor, int difficulty) {
        this._playerColor = playerColor;
        // Clamp difficulty between 1-5
        this._difficulty = Math.min(Math.max(1, difficulty), 5);
    }

    /**
     * Set the difficulty level
     *
     * @param difficulty Level between 1-5
     */
    @Override
    public void setDifficulty(int difficulty) {
        this._difficulty = Math.min(Math.max(1, difficulty), 5);
    }

    /**
     * Choose the best move for the AI using the Minimax algorithm
     *
     * @param gameState Current game state
     * @return The best move, or null if no legal moves
     */
    @Override
    public Move chooseMove(GameState gameState) {
        if (gameState.getCurrentPlayer() != this._playerColor) {
            throw new IllegalStateException("Not AI's turn to move");
        }

        long startTime = System.currentTimeMillis();

        // Get legal moves
        List<Move> legalMoves = gameState.getAllLegalMovesFor(this._playerColor);

        if (legalMoves.isEmpty()) {
            return null;
        }

        // If only one legal move, return it immediately
        if (legalMoves.size() == 1) {
            ensureMinimumThinkingTime(startTime);
            return legalMoves.get(0);
        }

        // For very low difficulty (level 1), just make a random move
        if (this._difficulty == 1) {
            Move randomMove = legalMoves.get(_random.nextInt(legalMoves.size()));
            ensureMinimumThinkingTime(startTime);
            return randomMove;
        }

        // For higher difficulties, use minimax with alpha-beta pruning
        int depth = this._difficulty;

        // Run minimax search
        MoveEvaluation evaluation = minimaxAlphaBeta(
                gameState,
                depth,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                true // maximizing player
        );

        // If no move was found (shouldn't happen), choose a random move
        Move bestMove = evaluation.getMove();
        if (bestMove == null && !legalMoves.isEmpty()) {
            bestMove = legalMoves.get(_random.nextInt(legalMoves.size()));
        }

        ensureMinimumThinkingTime(startTime);
        return bestMove;
    }

    /**
     * Choose the piece type for pawn promotion
     * AI will almost always choose Queen as it's the strongest piece
     *
     * @return The piece type to promote to (usually QUEEN)
     */
    @Override
    public EPieceType handlePromotion() {
        // For simplicity, AI always chooses Queen (strongest piece)
        return EPieceType.QUEEN;
    }

    /**
     * Make sure AI "thinks" for at least the minimum thinking time
     */
    private void ensureMinimumThinkingTime(long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        // ms
        int _minimumThinkingTime = 500;
        if (elapsedTime < _minimumThinkingTime) {
            try {
                Thread.sleep(_minimumThinkingTime - elapsedTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Minimax algorithm with alpha-beta pruning
     *
     * @param gameState Current game state
     * @param depth Remaining depth to search
     * @param alpha Best already explored option for maximizer
     * @param beta Best already explored option for minimizer
     * @param isMaximizing Whether current player is maximizing
     * @return Best score and move
     */
    private MoveEvaluation minimaxAlphaBeta(
            GameState gameState,
            int depth,
            int alpha,
            int beta,
            boolean isMaximizing) {

        // Base case: reached depth limit or game over
        if (depth == 0 || gameState.isGameOver()) {
            int score = BoardEvaluator.evaluate(gameState.getBoard(), this._playerColor);
            return new MoveEvaluation(score, null);
        }

        EPlayer currentPlayer = isMaximizing ? this._playerColor : this._playerColor.getOpponent();
        List<Move> legalMoves = gameState.getAllLegalMovesFor(currentPlayer);

        if (legalMoves.isEmpty()) {
            // No moves available, game is in stalemate or checkmate
            if (gameState.getBoard().isInCheck(currentPlayer)) {
                // Checkmate - worst scenario for current player
                return new MoveEvaluation(
                        isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE,
                        null
                );
            } else {
                // Stalemate - neutral value 0
                return new MoveEvaluation(0, null);
            }
        }

        Move bestMove = null;

        int bestScore;
        if (isMaximizing) {
            bestScore = Integer.MIN_VALUE;

            for (Move move : legalMoves) {
                // Create a copy of the game state to simulate the move
                GameState newGameState = gameState.copy();
                newGameState.makeMove(move);

                // Recursively get score for this move
                int score = minimaxAlphaBeta(
                        newGameState,
                        depth - 1,
                        alpha,
                        beta,
                        false
                ).getScore();

                // Update best score if better
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }

                // Update alpha
                alpha = Math.max(alpha, bestScore);

                // Prune if possible
                if (beta <= alpha) {
                    break;
                }
            }

        } else {
            bestScore = Integer.MAX_VALUE;

            for (Move move : legalMoves) {
                // Create a copy of the game state to simulate the move
                GameState newGameState = gameState.copy();
                newGameState.makeMove(move);

                // Recursively get score for this move
                int score = minimaxAlphaBeta(
                        newGameState,
                        depth - 1,
                        alpha,
                        beta,
                        true
                ).getScore();

                // Update best score if better
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }

                // Update beta
                beta = Math.min(beta, bestScore);

                // Prune if possible
                if (beta <= alpha) {
                    break;
                }
            }

        }
        return new MoveEvaluation(bestScore, bestMove);
    }

    /**
     * Helper class to store move and evaluation score together
     */
    private static class MoveEvaluation {
        private final int _score;
        private final Move _move;

        public MoveEvaluation(int score, Move move) {
            this._score = score;
            this._move = move;
        }

        public int getScore() {
            return _score;
        }

        public Move getMove() {
            return _move;
        }
    }
}