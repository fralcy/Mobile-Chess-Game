package com.example.chess_mobile.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.player.Player;

public class LocalChessBoardFragment extends ChessBoardFragment{
    private static final String BOARD_SIZE = "boardSize";
    private static final String MAIN_PLAYER = "mainPlayer";

    @NonNull
    public static ChessBoardFragment newInstance(int size, Player mainPlayer) {
        ChessBoardFragment fragment = new LocalChessBoardFragment();
        Bundle args = new Bundle();
        args.putInt(BOARD_SIZE, size);
        args.putSerializable(MAIN_PLAYER, mainPlayer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showDrawOfferDialog() {
        Log.d("MATCH_TYPE", String.valueOf(R.string.draw_confirm_message));
        Dialog dialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);
        ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(R.string.draw_confirm_message);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l -> {
            this._chessboardViewModel.setResult(Result.draw(EEndReason.DRAW));
            dialog.dismiss();
        });
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();
    }
}
