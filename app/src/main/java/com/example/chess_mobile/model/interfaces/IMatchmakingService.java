package com.example.chess_mobile.model.interfaces;

import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.model.logic.game_states.EPlayer;

public interface IMatchmakingService {
    default void searchForMatch(int timeMinutes, IOnMatchFoundListener listener) {
        // TODO: Implement actual matchmaking logic with Firebase

        // Simulate finding match after delay
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (listener != null) {
                PlayerChess opponent = new PlayerChess("opponent_id", "Random Player", EPlayer.BLACK);
                listener.onMatchFound(opponent);
            }
        }, 2000);
    }

    default void cancelSearch() {
        // TODO: Cancel ongoing search
    }
}