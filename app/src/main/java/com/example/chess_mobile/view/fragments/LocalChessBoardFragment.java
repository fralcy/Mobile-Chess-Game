package com.example.chess_mobile.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.view_model.implementations.LocalChessBoardViewModel;

public class LocalChessBoardFragment extends ChessBoardFragment {
    private static final String BOARD_SIZE = "boardSize";
    private static final String MAIN_PLAYER = "mainPlayer";
    private static final String TYPE = "matchType";

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
        // Trong local match, cho phép cả 2 màu đều có thể chơi
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
        super.drawBoard(board); // Gọi method cha để vẽ board bình thường

        // Sau khi vẽ board, update critical hit highlight
        updateCriticalHitHighlight();
    }
    @Override
    protected void showLastMoveColor() {
        super.showLastMoveColor(); // Gọi logic cha trước

        // Sau đó apply critical hit highlight
        highlightCriticalHitPiece();
    }

    @Override
    protected void showHighlights() {
        super.showHighlights(); // Gọi logic cha trước

        // Sau đó apply critical hit highlight
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
                    // Highlight với màu xanh ngọc đặc biệt cho critical hit
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
                    return; // Không cho phép select
                }
            }
        }

        super.onFromPositionSelected(pos);
    }

    @Override
    public void showDrawOfferDialog() {
        Log.d("LOCAL_MATCH", "Draw offer in local match");
        Dialog dialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);

        String currentPlayerName = this._chessboardViewModel.getCurrentPlayer() == EPlayer.WHITE ?
                "White Player" : "Black Player";
        String message = currentPlayerName + " offers a draw. Do you accept?";

        ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(message);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l -> {
            this._chessboardViewModel.setResult(Result.draw(EEndReason.DRAW));
            dialog.dismiss();
        });
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void showResignationDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);

        String currentPlayerName = this._chessboardViewModel.getCurrentPlayer() == EPlayer.WHITE ?
                "White Player" : "Black Player";
        String message = currentPlayerName + " wants to resign. Confirm?";

        ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(message);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l -> {
            EPlayer winner = this._chessboardViewModel.getCurrentPlayer() == EPlayer.WHITE ?
                    EPlayer.BLACK : EPlayer.WHITE;
            this._chessboardViewModel.setResult(Result.win(winner, EEndReason.RESIGNATION));
            dialog.dismiss();
        });
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void showConfirmOfferDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);

        String currentPlayerName = this._chessboardViewModel.getCurrentPlayer() == EPlayer.WHITE ?
                "White Player" : "Black Player";
        String message = currentPlayerName + " wants to offer a draw. Confirm?";

        ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(message);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l -> {
            showDrawOfferDialog();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();
    }
}