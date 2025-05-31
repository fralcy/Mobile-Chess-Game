package com.example.chess_mobile.dto.request;

import java.io.Serializable;

public class CancelMatchRequest  {
    private String matchId;
    private String playerId;

    public CancelMatchRequest() {
    }

    public CancelMatchRequest(String matchId, String playerId) {
        this.matchId = matchId;
        this.playerId = playerId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
