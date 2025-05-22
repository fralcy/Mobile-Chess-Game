package com.example.chess_mobile.view_model;

import android.content.Context;
import android.widget.Toast;

public interface IRankedMatchViewModel {
    default void onTimeSelected(int minutes, Context context) {
        String message = "Selected " + minutes + " minutes for ranked match";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    default void onPlayButtonClicked(Context context) {
        Toast.makeText(context, "Searching for ranked match...", Toast.LENGTH_SHORT).show();
        searchForMatch(context);
    }

    default void searchForMatch(Context context) {
        // TODO: Implement actual matchmaking logic
        Toast.makeText(context, "Looking for opponent...", Toast.LENGTH_LONG).show();
    }

    default void cancelMatchSearch(Context context) {
        Toast.makeText(context, "Match search cancelled", Toast.LENGTH_SHORT).show();
    }
}