package com.example.chess_mobile.dto.request;

public class JoinMatchRequest {
    private String playerId;
    public JoinMatchRequest() {

    }
    public JoinMatchRequest(String playerId) {
        this.playerId= playerId;
    }
    public String getPlayerId() {
        return this.playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId=playerId;
    }
}
