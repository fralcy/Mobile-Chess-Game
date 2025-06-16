package com.example.chess_mobile.view_model.implementations;

import com.example.chess_mobile.model.logic.features.CriticalHitSystem;
import com.example.chess_mobile.model.logic.features.EmotionSystem;
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
    private Position _criticalHitPiecePosition = null;

    // Emotion System
    private boolean _emotionEnabled = false;
    private EmotionSystem.EmotionType _lastEmotionShown = null;
    private Position _lastEmotionPosition = null;
    private String _lastEmotionMessage = "";

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
        updateCurrentDisplayPlayer();
        resetCriticalHitState();
        resetEmotionState();
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
        boolean isCriticalHit = false;

        if (_criticalHitEnabled && isCapture && movingPiece != null &&
                !(_hasCriticalHit && _criticalHitPlayer == currentPlayer)) {

            boolean criticalHit = CriticalHitSystem.isCriticalHit(movingPiece.getType(), true);

            if (criticalHit) {
                Position critPos = move.getToPos();
                List<Move> critMoves = currentState.getLegalMovesForPiece(critPos);

                if (!critMoves.isEmpty()) {
                    shouldKeepTurn = true;
                    isCriticalHit = true;
                    _hasCriticalHit = true;
                    _criticalHitPlayer = currentPlayer;
                    _criticalHitPiecePosition = move.getToPos(); // Lưu vị trí quân critical hit
                }
            }
        }

        // Xử lý kết thúc critical hit turn
        if (_hasCriticalHit && _criticalHitPlayer == currentPlayer) {
            if (!isCapture || !shouldKeepTurn) {
                resetCriticalHitState();
                shouldKeepTurn = false;
            }
        }

        // XỬ LÝ EMOTION SYSTEM
        handleEmotionAfterMove(move, isCapture, isCriticalHit, movingPiece);

        // Chuyển lượt chỉ khi KHÔNG có critical hit
        if (!shouldKeepTurn) {
            currentState.setCurrentPlayer(currentPlayer.getOpponent());
        }

        // Update game state
        currentState.updateAfterMove(captureOrPawn);
        this._result.setValue(currentState.getResult());
        updateCurrentDisplayPlayer();
    }

    /**
     * Xử lý emotion sau khi di chuyển
     */
    private void handleEmotionAfterMove(Move move, boolean isCapture, boolean isCriticalHit, Piece movingPiece) {
        if (!_emotionEnabled || movingPiece == null) {
            return;
        }

        // Xác định context
        EmotionSystem.EmotionContext context;
        if (isCriticalHit) {
            context = EmotionSystem.EmotionContext.CRITICAL_HIT;
        } else if (isCapture) {
            context = EmotionSystem.EmotionContext.CAPTURE_PIECE;
        } else {
            context = EmotionSystem.EmotionContext.NORMAL_MOVE;
        }

        // Kiểm tra có nên hiện biểu cảm không
        if (!EmotionSystem.shouldShowEmotion(context)) {
            return;
        }

        // Tính toán biểu cảm
        EmotionSystem.EmotionType emotion = EmotionSystem.calculateEmotion(
                movingPiece.getType(), context
        );

        if (emotion != null) {
            _lastEmotionShown = emotion;
            _lastEmotionPosition = move.getToPos();
            _lastEmotionMessage = createEmotionMessage(emotion, movingPiece.getType(), context);
        }
    }

    /**
     * Tạo message cho emotion
     */
    private String createEmotionMessage(EmotionSystem.EmotionType emotion,
                                        com.example.chess_mobile.model.logic.pieces.EPieceType pieceType,
                                        EmotionSystem.EmotionContext context) {
        String pieceNameVn = getPieceNameVietnamese(pieceType);

        return switch (emotion) {
            case HAPPY -> pieceNameVn + " vui vẻ " + emotion.getEmoji();
            case ANGRY -> pieceNameVn + " tức giận " + emotion.getEmoji();
            case SURPRISED -> pieceNameVn + " ngạc nhiên " + emotion.getEmoji();
            case COOL -> pieceNameVn + " ngầu quá " + emotion.getEmoji();
            case WORRIED -> pieceNameVn + " lo lắng " + emotion.getEmoji();
            case NEUTRAL -> pieceNameVn + " bình tĩnh " + emotion.getEmoji();
        };
    }

    /**
     * Lấy tên quân cờ tiếng Việt
     */
    private String getPieceNameVietnamese(com.example.chess_mobile.model.logic.pieces.EPieceType pieceType) {
        return switch (pieceType) {
            case QUEEN -> "Hậu";
            case ROOK -> "Xe";
            case BISHOP -> "Tượng";
            case KNIGHT -> "Mã";
            case PAWN -> "Tốt";
            case KING -> "Vua";
        };
    }

    @Override
    public List<Move> getLegalMovesForPiece(Position pos) {
        // Nếu đang trong critical hit turn, chỉ cho phép đi quân critical hit
        if (_hasCriticalHit && _criticalHitPlayer == getCurrentPlayer()) {
            if (_criticalHitPiecePosition != null && !pos.equals(_criticalHitPiecePosition)) {
                return new ArrayList<>();
            }
        }

        return super.getLegalMovesForPiece(pos);
    }

    // ============ CRITICAL HIT METHODS ============
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

    // ============ EMOTION METHODS ============
    public void setEmotionEnabled(boolean enabled) {
        this._emotionEnabled = enabled;
        if (!enabled) {
            resetEmotionState();
        }
    }

    public boolean isEmotionEnabled() {
        return _emotionEnabled;
    }

    public EmotionSystem.EmotionType getLastEmotionShown() {
        return _lastEmotionShown;
    }

    public Position getLastEmotionPosition() {
        return _lastEmotionPosition;
    }

    public String getLastEmotionMessage() {
        return _lastEmotionMessage;
    }

    public boolean hasNewEmotion() {
        return _lastEmotionShown != null && _lastEmotionPosition != null;
    }

    public void clearEmotionData() {
        resetEmotionState();
    }

    private void resetEmotionState() {
        _lastEmotionShown = null;
        _lastEmotionPosition = null;
        _lastEmotionMessage = "";
    }

    // ============ DISPLAY PLAYER MANAGEMENT ============
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