package com.example.chess_mobile.view_model.implementations;

import com.example.chess_mobile.model.logic.features.CriticalHitSystem;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.player.PlayerChess;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LocalChessBoardViewModel extends ChessBoardViewModel {
    private PlayerChess _currentDisplayPlayer;

    // Critical Hit System
    private boolean _criticalHitEnabled = false;
    private boolean _hasCriticalHit = false;
    private String _lastCriticalHitMessage = "";
    private EPlayer _criticalHitPlayer = null;
    private Position _criticalHitPiecePosition = null; // Vị trí quân được critical hit

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
        updateCurrentDisplayPlayer();
        resetCriticalHitState();
    }

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = getGameState().getValue();
        if (currentState == null || currentState.isGameOver()) return;

        Piece movingPiece = currentState.getBoard().getPiece(move.getFromPos());
        boolean isCapture = !currentState.getBoard().isEmpty(move.getToPos());
        EPlayer currentPlayer = getCurrentPlayer();

        // Thực hiện nước đi KHÔNG chuyển lượt
        boolean captureOrPawn = currentState.makeMoveWithoutTurnChange(move);

        // XỬ LÝ CRITICAL HIT
        boolean shouldKeepTurn = false;

        // Chỉ cho phép critical hit nếu:
        // 1. Critical hit được bật
        // 2. Có bắt quân
        // 3. Có quân di chuyển
        // 4. KHÔNG đang trong critical hit turn (ngăn stack)
        if (_criticalHitEnabled && isCapture && movingPiece != null &&
                !(_hasCriticalHit && _criticalHitPlayer == currentPlayer)) {

            boolean criticalHit = CriticalHitSystem.isCriticalHit(movingPiece.getType(), true);

            if (criticalHit) {
                // KIỂM TRA QUÂN CRITICAL HIT CÓ NƯỚC ĐI HỢP LỆ KHÔNG
                Position critPos = move.getToPos();
                List<Move> critMoves = currentState.getLegalMovesForPiece(critPos);

                if (!critMoves.isEmpty()) {
                    // Chỉ cho critical hit khi quân còn nước đi
                    shouldKeepTurn = true;
                    _hasCriticalHit = true;
                    _criticalHitPlayer = currentPlayer;
                    _criticalHitPiecePosition = move.getToPos(); // Lưu vị trí quân critical hit
                }
                // Nếu quân không còn nước đi -> bỏ qua lượt thưởng (không làm gì)
            }
        }

        // Xử lý kết thúc critical hit turn
        if (_hasCriticalHit && _criticalHitPlayer == currentPlayer) {
            // Kết thúc critical hit nếu:
            // 1. Không bắt quân trong lượt critical hit, HOẶC
            // 2. Bắt quân nhưng không được critical hit mới
            if (!isCapture || !shouldKeepTurn) {
                resetCriticalHitState();
                shouldKeepTurn = false;
            }
        }

        // Chuyển lượt chỉ khi KHÔNG có critical hit
        if (!shouldKeepTurn) {
            currentState.setCurrentPlayer(currentPlayer.getOpponent());
        }

        // Update game state
        currentState.updateAfterMove(captureOrPawn);
        this._result.setValue(currentState.getResult());
        updateCurrentDisplayPlayer();
    }

    @Override
    public List<Move> getLegalMovesForPiece(Position pos) {
        // Nếu đang trong critical hit turn, chỉ cho phép đi quân critical hit
        if (_hasCriticalHit && _criticalHitPlayer == getCurrentPlayer()) {
            if (_criticalHitPiecePosition != null && !pos.equals(_criticalHitPiecePosition)) {
                return new ArrayList<>(); // Không cho phép đi quân khác
            }
        }

        return super.getLegalMovesForPiece(pos);
    }

    // Critical Hit Methods
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

    public Position getCriticalHitPiecePosition() {
        return _criticalHitPiecePosition;
    }

    public EPlayer getCriticalHitPlayer() {
        return _criticalHitPlayer;
    }

    private void resetCriticalHitState() {
        _hasCriticalHit = false;
        _criticalHitPlayer = null;
        _lastCriticalHitMessage = "";
        _criticalHitPiecePosition = null;
    }

    public void endCriticalHitTurn() {
        if (_hasCriticalHit) {
            resetCriticalHitState();

            GameState currentState = getGameState().getValue();
            if (currentState != null) {
                EPlayer nextPlayer = getCurrentPlayer().getOpponent();
                currentState.setCurrentPlayer(nextPlayer);
            }
            updateCurrentDisplayPlayer();
        }
    }

    // Display Player Management
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