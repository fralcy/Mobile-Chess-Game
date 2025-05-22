package com.example.chess_mobile.view_model;

import android.content.Context;
import android.content.Intent;
import com.example.chess_mobile.view.activities.*;

public interface IGameModeSelectionViewModel {
    default void onRankedModeSelected(Context context) {
        Intent intent = new Intent(context, RankedMatchActivity.class);
        context.startActivity(intent);
    }

    default void onFriendModeSelected(Context context) {
        Intent intent = new Intent(context, FriendMatchActivity.class);
        context.startActivity(intent);
    }

    default void onAIModeSelected(Context context) {
        Intent intent = new Intent(context, AIMatchActivity.class);
        context.startActivity(intent);
    }

    default void onLocalModeSelected(Context context) {
        Intent intent = new Intent(context, LocalMatchActivity.class);
        context.startActivity(intent);
    }

    default void onProfileButtonClicked(Context context) {
        // TODO: Navigate to profile activity when created
        android.widget.Toast.makeText(context, "Profile feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
    }
}