package com.example.chess_mobile.model.ai;

import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.EPieceType;

/**
 * Interface for chess AI engines
 */
public interface IAIEngine {
    /**
     * Set the difficulty level of the AI
     *
     * @param difficulty Level from 1-5, higher is stronger
     */
    void setDifficulty(int difficulty);

    /**
     * Make the AI choose the best move for the current position
     *
     * @param gameState Current game state
     * @return The best move according to the AI
     */
    Move chooseMove(GameState gameState);

    /**
     * Let the AI choose a piece type for pawn promotion
     *
     * @return The piece type to promote to
     */
    EPieceType handlePromotion();
}