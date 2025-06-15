package com.example.chess_mobile.view_model.implementations;

import com.example.chess_mobile.model.logic.features.CriticalHitSystem;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.player.PlayerChess;

import java.time.Duration;

public class LocalChessBoardViewModel extends ChessBoardViewModel {
    private PlayerChess _currentDisplayPlayer;
    private boolean _criticalHitEnabled = false;
    private boolean _hasCriticalHit = false;
    private String _lastCriticalHitMessage = "";
    private EPlayer _criticalHitPlayer = null; // Ai đang trong trạng thái chí mạng

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
        updateCurrentDisplayPlayer();

        // Reset critical hit state khi bắt đầu game mới
        _hasCriticalHit = false;
        _criticalHitPlayer = null;
        _lastCriticalHitMessage = "";
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

        // Thực hiện nước đi bình thường - GameState vẫn hoạt động như cũ
        super.gameStateMakeMove(move);

        // XỬ LÝ CRITICAL HIT CHỈ TRONG VIEWMODEL
        if (_criticalHitEnabled && isCapture && movingPiece != null) {
            boolean criticalHit = CriticalHitSystem.isCriticalHit(movingPiece.getType(), true);

            if (criticalHit) {
                // Set critical hit state
                _hasCriticalHit = true;
                _criticalHitPlayer = currentPlayer;
                String pieceName = CriticalHitSystem.getPieceNameInVietnamese(movingPiece.getType());
                _lastCriticalHitMessage = pieceName + " chí mạng! Được đi thêm một lượt!";

                // HACK: Đảo ngược lại current player trong GameState
                // vì super.gameStateMakeMove() đã chuyển lượt
                currentState = getGameState().getValue();
                if (currentState != null) {
                    // Sử dụng reflection hoặc method có sẵn để set lại current player
                    forceSetCurrentPlayer(currentState, currentPlayer);
                }
                return; // Không cập nhật display player
            }
        }

        // Kiểm tra xem có phải đang trong critical hit turn không
        if (_hasCriticalHit && _criticalHitPlayer == currentPlayer) {
            // Nếu không bắt quân trong lượt critical hit, kết thúc critical hit
            if (!isCapture) {
                endCriticalHitTurn();
            }
        } else {
            // Reset critical hit nếu không trong trạng thái critical hit
            _hasCriticalHit = false;
            _criticalHitPlayer = null;
            _lastCriticalHitMessage = "";
        }

        updateCurrentDisplayPlayer();
    }

    /**
     * Force set current player trong GameState (hack để không sửa GameState)
     */
    private void forceSetCurrentPlayer(GameState gameState, EPlayer player) {
        try {
            // Option 1: Nếu có setter
            // gameState.setCurrentPlayer(player);

            // Option 2: Sử dụng reflection (nếu field private)
            java.lang.reflect.Field field = GameState.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameState, player);
        } catch (Exception e) {
            // Fallback: Log error nhưng vẫn tiếp tục
            android.util.Log.e("CRITICAL_HIT", "Cannot set current player: " + e.getMessage());
        }
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

            updateCurrentDisplayPlayer();
        }
    }

    private void updateCurrentDisplayPlayer() {
        EPlayer currentPlayerColor = getCurrentPlayer();
        if (currentPlayerColor != null) {
            PlayerChess main = getMainPlayer();
            PlayerChess opponent = getOpponentPlayer();

            if (main != null && opponent != null) {
                _currentDisplayPlayer = (currentPlayerColor == main.getColor()) ? main : opponent;
            }
        }
    }

    public PlayerChess getCurrentDisplayPlayer() {
        return _currentDisplayPlayer;
    }
}