package com.example.chess_mobile.dto.request;

public class PlayerUpdateRequest {
    private String playerName;
    public PlayerUpdateRequest() {

    }
    public PlayerUpdateRequest(String playerName) {

    }
    public String getPlayerName() {
        return this.playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName=playerName;
    }
}
