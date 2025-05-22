package com.example.chess_mobile.utils.interfaces;

import android.content.Context;
import android.content.Intent;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.view.activities.*;

public interface INavigationHelper {
    default void navigateToGameModeSelection(Context context) {
        Intent intent = new Intent(context, GameModeSelectionActivity.class);
        context.startActivity(intent);
    }

    default void navigateToRankedMatch(Context context) {
        Intent intent = new Intent(context, RankedMatchActivity.class);
        context.startActivity(intent);
    }

    default void navigateToFriendMatch(Context context) {
        Intent intent = new Intent(context, FriendMatchActivity.class);
        context.startActivity(intent);
    }

    default void navigateToAIMatch(Context context) {
        Intent intent = new Intent(context, AIMatchActivity.class);
        context.startActivity(intent);
    }

    default void navigateToLocalMatch(Context context) {
        Intent intent = new Intent(context, LocalMatchActivity.class);
        context.startActivity(intent);
    }

    default void navigateToChessGame(Context context, Player mainPlayer, Player opponent) {
        Intent intent = new Intent(context, RoomChessActivity.class);
        intent.putExtra("MAIN_PLAYER", mainPlayer);
        intent.putExtra("OPPONENT_PLAYER", opponent);
        context.startActivity(intent);
    }
}