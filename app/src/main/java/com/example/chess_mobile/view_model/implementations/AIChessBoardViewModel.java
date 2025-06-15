package com.example.chess_mobile.view_model.implementations;

import com.example.chess_mobile.model.ai.ChessAIPlayer;
import com.example.chess_mobile.model.logic.features.CriticalHitSystem;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.player.PlayerChess;

import java.time.Duration;

public class AIChessBoardViewModel extends ChessBoardViewModel {
    private ChessAIPlayer _aiPlayer;
    private int _aiDifficulty = 1;

    // Critical Hit variables
    private boolean _criticalHitEnabled = false;
    private boolean _hasCriticalHit = false;
    private String _lastCriticalHitMessage = "";
    private EPlayer _criticalHitPlayer = null;

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);

        // Setup AI player
        EPlayer aiColor = opponent.getColor();
        _aiPlayer = new ChessAIPlayer(opponent.getName(), aiColor, _aiDifficulty);

        // Reset critical hit state
        _hasCriticalHit = false;
        _criticalHitPlayer = null;
        _lastCriticalHitMessage = "";

        // Nếu AI là WHITE (đi trước), thực hiện nước đi đầu tiên
        if (_aiPlayer.getColor() == EPlayer.WHITE) {
            makeAIMove();
        }
    }

    public void setCriticalHitEnabled(boolean enabled) {
        this._criticalHitEnabled = enabled;
    }

    public boolean isCriticalHitEnabled() {
        return _criticalHitEnabled;
    }

    public boolean hasCriticalHit() {
        return _hasCriticalHit;
    }

    public String getLastCriticalHitMessage() {
        return _lastCriticalHitMessage;
    }

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = getGameState().getValue();
        if (currentState == null || currentState.isGameOver()) return;

        Piece movingPiece = currentState.getBoard().getPiece(move.getFromPos());
        boolean isCapture = !currentState.getBoard().isEmpty(move.getToPos());
        EPlayer currentPlayer = getCurrentPlayer();

        // Thực hiện nước đi bình thường
        super.gameStateMakeMove(move);

        // XỬ LÝ CRITICAL HIT CHỈ TRONG VIEWMODEL
        boolean criticalHitOccurred = false;

        if (_criticalHitEnabled && isCapture && movingPiece != null) {
            boolean criticalHit = CriticalHitSystem.isCriticalHit(movingPiece.getType(), true);

            if (criticalHit) {
                criticalHitOccurred = true;
                _hasCriticalHit = true;
                _criticalHitPlayer = currentPlayer;
                String pieceName = CriticalHitSystem.getPieceNameInVietnamese(movingPiece.getType());
                _lastCriticalHitMessage = pieceName + " chí mạng! Được đi thêm một lượt!";

                // Đảo ngược lại current player trong GameState
                currentState = getGameState().getValue();
                if (currentState != null) {
                    forceSetCurrentPlayer(currentState, currentPlayer);
                }
            }
        }

        // Kiểm tra kết thúc critical hit turn
        if (_hasCriticalHit && _criticalHitPlayer == currentPlayer && !isCapture) {
            endCriticalHitTurn();
            criticalHitOccurred = false; // Reset vì đã kết thúc
        }

        // Xử lý AI move - chỉ khi không có critical hit hoặc đã kết thúc critical hit
        if (!criticalHitOccurred && !isGameOver().orElse(true)) {
            EPlayer newCurrentPlayer = getCurrentPlayer();
            if (newCurrentPlayer == _aiPlayer.getColor()) {
                makeAIMove();
            }
        }

        // Nếu có critical hit và là lượt AI, cho AI tiếp tục đi
        if (criticalHitOccurred && _criticalHitPlayer == _aiPlayer.getColor()) {
            makeAIMove();
        }
    }

    /**
     * Force set current player trong GameState
     */
    private void forceSetCurrentPlayer(GameState gameState, EPlayer player) {
        gameState.setCurrentPlayer(player);
    }

    /**
     * Kết thúc lượt critical hit
     */
    public void endCriticalHitTurn() {
        if (_hasCriticalHit) {
            _hasCriticalHit = false;
            _criticalHitPlayer = null;
            _lastCriticalHitMessage = "";

            // Chuyển lượt bình thường
            GameState currentState = getGameState().getValue();
            if (currentState != null) {
                EPlayer nextPlayer = getCurrentPlayer().getOpponent();
                forceSetCurrentPlayer(currentState, nextPlayer);
            }
        }
    }

    private void makeAIMove() {
        if (_aiPlayer != null && getBoard() != null) {
            _aiPlayer.calculateMove(getBoard(), move -> {
                if (move != null && !isGameOver().orElse(true)) {
                    super.gameStateMakeMove(move);
                }
            });
        }
    }

    public void setAIDifficulty(int difficulty) {
        this._aiDifficulty = difficulty;
        if (_aiPlayer != null) {
            EPlayer aiColor = _aiPlayer.getColor();
            String aiName = "AI Level " + difficulty;
            _aiPlayer = new ChessAIPlayer(aiName, aiColor, difficulty);
        }
    }

    public boolean isAIThinking() {
        return _aiPlayer != null && _aiPlayer.isThinking();
    }
}