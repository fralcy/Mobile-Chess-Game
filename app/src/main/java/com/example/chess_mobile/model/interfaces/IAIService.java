package com.example.chess_mobile.model.interfaces;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.moves.NormalMove;

public interface IAIService {
    default Move calculateMove(Board board, int difficulty) {
        // TODO: Implement actual AI logic
        // For now, return a random valid move
        return getRandomMove(board);
    }

    default void setDifficulty(int level) {
        // TODO: Store difficulty level
    }

    default boolean isThinking() {
        // TODO: Return if AI is currently calculating
        return false;
    }

    default Move getRandomMove(Board board) {
        // Simple random move for testing
        var positions = board.getPiecePositionsFor(EPlayer.BLACK);
        if (!positions.isEmpty()) {
            Position from = positions.get(0);
            var piece = board.getPiece(from);
            var moves = piece.getMoves(from, board);
            if (!moves.isEmpty()) {
                return moves.get(0);
            }
        }
        return new NormalMove(new Position(0, 0), new Position(0, 1));
    }
}