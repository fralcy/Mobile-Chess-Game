package com.example.chess_mobile.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;

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
        // Trong local match, cho phép cả 2 màng đều có thể chơi
        // Không cần kiểm tra main player color như online match

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
            // Trong local match, trực tiếp hiển thị dialog cho đối thủ
            showDrawOfferDialog();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();
    }
}