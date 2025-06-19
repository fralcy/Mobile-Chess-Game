package com.example.chess_mobile.dto.response;

import java.util.Date;

public class MatchHistory {
    private String matchId;
    private PlayerResponse playerWhite;
    private PlayerResponse playerBlack;
    private String matchState;
    private String matchType;
    private Long playTime;
    private Long numberOfTurns;
    private Date matchTime;

    public MatchHistory(String matchId, String matchState, Date matchTime, String matchType, Long numberOfTurns, PlayerResponse playerBlack, PlayerResponse playerWhite, Long playTime) {
        this.matchId = matchId;
        this.matchState = matchState;
        this.matchTime = matchTime;
        this.matchType = matchType;
        this.numberOfTurns = numberOfTurns;
        this.playerBlack = playerBlack;
        this.playerWhite = playerWhite;
        this.playTime = playTime;
    }

    public MatchHistory() {
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchState() {
        return matchState;
    }

    public void setMatchState(String matchState) {
        this.matchState = matchState;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Long getNumberOfTurns() {
        return numberOfTurns;
    }

    public void setNumberOfTurns(Long numberOfTurns) {
        this.numberOfTurns = numberOfTurns;
    }

    public PlayerResponse getPlayerBlack() {
        return playerBlack;
    }

    public void setPlayerBlack(PlayerResponse playerBlack) {
        this.playerBlack = playerBlack;
    }

    public PlayerResponse getPlayerWhite() {
        return playerWhite;
    }

    public void setPlayerWhite(PlayerResponse playerWhite) {
        this.playerWhite = playerWhite;
    }

    public Long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Long playTime) {
        this.playTime = playTime;
    }
}
