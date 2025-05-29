package com.example.chess_mobile.view_model.interfaces;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.chess_mobile.view.activities.RoomChessActivity;

public interface ILocalMatchViewModel {
    default void onWhiteTimeSelected(int minutes, Context context) {
        Toast.makeText(context, "White player time: " + minutes + " minutes", Toast.LENGTH_SHORT).show();
    }

    default void onBlackTimeSelected(int minutes, Context context) {
        Toast.makeText(context, "Black player time: " + minutes + " minutes", Toast.LENGTH_SHORT).show();
    }

    default void onPlayButtonClicked(Context context) {
        Toast.makeText(context, "Starting local match...", Toast.LENGTH_SHORT).show();
        initializeLocalGame(context);
    }

    default void initializeLocalGame(Context context) {
        // TODO: Initialize local game with selected time settings
        Intent intent = new Intent(context, RoomChessActivity.class);
        intent.putExtra("GAME_MODE", "LOCAL");
        context.startActivity(intent);
    }
}