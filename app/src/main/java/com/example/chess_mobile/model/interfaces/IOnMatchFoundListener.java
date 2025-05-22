package com.example.chess_mobile.model.interfaces;

import com.example.chess_mobile.model.player.Player;

public interface IOnMatchFoundListener {
    void onMatchFound(Player opponent);
    void onMatchSearchFailed(String error);
}