package com.example.chess_mobile.view.fragments;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.view.components.EmotionManager;
import com.example.chess_mobile.view_model.implementations.LocalChessBoardViewModel;

public class LocalChessBoardFragment extends ChessBoardFragment {
    private static final String BOARD_SIZE = "boardSize";
    private static final String MAIN_PLAYER = "mainPlayer";
    private static final String TYPE = "matchType";

    private EmotionManager _emotionManager;

    @NonNull
    public static LocalChessBoardFragment newInstance(int size, PlayerChess mainPlayer, EMatch matchType) {
        LocalChessBoardFragment fragment = new LocalChessBoardFragment();
        Bundle args = new Bundle();
        args.putInt(BOARD_SIZE, size);
        args.putSerializable(MAIN_PLAYER, mainPlayer);
        args.putSerializable(TYPE, matchType);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void handleSquareClick(int row, int col) {
        // Clear emotion data trước khi xử lý click mới
        if (_chessboardViewModel instanceof LocalChessBoardViewModel) {
            ((LocalChessBoardViewModel) _chessboardViewModel).clearEmotionData();
        }

        showLastMoveColor();
        if (this._selectedPos == null) {
            showLastMoveColor();
            onFromPositionSelected(new Position(row, col));
        } else {
            onToPositionSelected(new Position(row, col));
            showLastMoveColor();
        }
    }

    @Override
    protected void drawBoard(Board board) {
        super.drawBoard(board);

        // Update critical hit highlight
        updateCriticalHitHighlight();

        // Update emotion display
        updateEmotionDisplay();
    }

    @Override
    protected void showLastMoveColor() {
        super.showLastMoveColor();
        highlightCriticalHitPiece();
    }

    @Override
    protected void showHighlights() {
        super.showHighlights();
        highlightCriticalHitPiece();
    }

    /**
     * Highlight quân đang trong critical hit state
     */
    private void highlightCriticalHitPiece() {
        if (!(_chessboardViewModel instanceof LocalChessBoardViewModel)) {
            return;
        }

        LocalChessBoardViewModel localViewModel = (LocalChessBoardViewModel) _chessboardViewModel;

        if (localViewModel.hasCriticalHit()) {
            Position critPos = localViewModel.getCriticalHitPiecePosition();
            EPlayer critPlayer = localViewModel.getCriticalHitPlayer();
            EPlayer currentPlayer = localViewModel.getCurrentPlayer();

            if (critPos != null && critPlayer == currentPlayer) {
                int boardRow = reversed ? (7 - critPos.row()) : critPos.row();
                int boardCol = reversed ? (7 - critPos.column()) : critPos.column();

                if (boardRow >= 0 && boardRow < _size && boardCol >= 0 && boardCol < _size) {
                    _squares[boardRow][boardCol].setBackgroundColor(0xFF03DAC5); // Material Teal
                }
            }
        }
    }

    /**
     * Hiển thị message critical hit
     */
    private void updateCriticalHitMessage() {
        if (!(_chessboardViewModel instanceof LocalChessBoardViewModel)) {
            return;
        }

        LocalChessBoardViewModel localViewModel = (LocalChessBoardViewModel) _chessboardViewModel;

        if (localViewModel.hasCriticalHit()) {
            String message = localViewModel.getLastCriticalHitMessage();
            EPlayer critPlayer = localViewModel.getCriticalHitPlayer();
            EPlayer currentPlayer = localViewModel.getCurrentPlayer();

            if (!message.isEmpty() && critPlayer == currentPlayer) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Update critical hit highlight và message
     */
    private void updateCriticalHitHighlight() {
        highlightCriticalHitPiece();
        updateCriticalHitMessage();
    }

    /**
     * Update emotion display
     */
    private void updateEmotionDisplay() {
        if (!(_chessboardViewModel instanceof LocalChessBoardViewModel) || getEmotionManager() == null) {
            return;
        }

        LocalChessBoardViewModel localViewModel = (LocalChessBoardViewModel) _chessboardViewModel;

        if (localViewModel.hasNewEmotion()) {
            Position emotionPos = localViewModel.getLastEmotionPosition();
            var emotionType = localViewModel.getLastEmotionShown();
            String emotionMessage = localViewModel.getLastEmotionMessage();

            if (emotionPos != null && emotionType != null) {
                // Convert position for display (handle board reversal)
                int displayRow = reversed ? (7 - emotionPos.row()) : emotionPos.row();
                int displayCol = reversed ? (7 - emotionPos.column()) : emotionPos.column();
                Position displayPos = new Position(displayRow, displayCol);

                // Hiển thị emotion
                if (emotionType == com.example.chess_mobile.model.logic.features.EmotionSystem.EmotionType.COOL) {
                    getEmotionManager().showCriticalHitEmotion(displayPos);
                } else {
                    getEmotionManager().showEmotion(displayPos, emotionType);
                }

                // Hiển thị message nếu có
                if (!emotionMessage.isEmpty() && getContext() != null) {
                    Toast.makeText(getContext(), emotionMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onFromPositionSelected(Position pos) {
        // Kiểm tra nếu đang trong critical hit turn và chọn sai quân
        if (_chessboardViewModel instanceof LocalChessBoardViewModel) {
            LocalChessBoardViewModel localViewModel = (LocalChessBoardViewModel) _chessboardViewModel;

            if (localViewModel.hasCriticalHit()) {
                Position critPos = localViewModel.getCriticalHitPiecePosition();
                EPlayer critPlayer = localViewModel.getCriticalHitPlayer();
                EPlayer currentPlayer = localViewModel.getCurrentPlayer();

                if (critPlayer == currentPlayer && critPos != null && !pos.equals(critPos)) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Chỉ được đi quân vừa critical hit!",
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
        }

        super.onFromPositionSelected(pos);
    }

    /**
     * Set emotion enabled state
     */
    public void setEmotionEnabled(boolean enabled) {
        if (getEmotionManager() != null) {
            getEmotionManager().setEmotionEnabled(enabled);
        }

        if (_chessboardViewModel instanceof LocalChessBoardViewModel) {
            ((LocalChessBoardViewModel) _chessboardViewModel).setEmotionEnabled(enabled);
        }
    }

    /**
     * Clear all emotions from board
     */
    public void clearAllEmotions() {
        if (getEmotionManager() != null) {
            getEmotionManager().clearAllEmotions();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clear emotions khi destroy
        clearAllEmotions();
    }
    private EmotionManager getEmotionManager() {
        if (_emotionManager == null && getContext() != null && _gridLayout != null) {
            _emotionManager = new EmotionManager(getContext(), _gridLayout);

            // Set emotion enabled state từ ViewModel
            if (_chessboardViewModel instanceof LocalChessBoardViewModel) {
                LocalChessBoardViewModel localViewModel = (LocalChessBoardViewModel) _chessboardViewModel;
                _emotionManager.setEmotionEnabled(localViewModel.isEmotionEnabled());
            }
        }
        return _emotionManager;
    }
}