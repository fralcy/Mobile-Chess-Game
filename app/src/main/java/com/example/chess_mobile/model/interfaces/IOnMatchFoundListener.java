package com.example.chess_mobile.model.interfaces;

import com.example.chess_mobile.model.player.PlayerChess;

public interface IOnMatchFoundListener {
    void onMatchFound(PlayerChess opponent);
    void onMatchSearchFailed(String error);
}