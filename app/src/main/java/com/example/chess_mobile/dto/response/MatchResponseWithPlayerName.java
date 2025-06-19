package com.example.chess_mobile.dto.response;

import java.util.Date;

public class MatchResponseWithPlayerName {
    private String matchId;
    private String playerWhiteName;
    private String playerBlackName;
    private String matchState;
    private int numberOfTurns;
    private int playTime;
    private Date matchTime;
    private String ErrorMessage;

    public MatchResponseWithPlayerName() {

    }

    public MatchResponseWithPlayerName(String errorMessage, String matchId, String matchState, Date matchTime, int numberOfTurns, String playerBlackName, String playerWhiteName, int playTime) {
        ErrorMessage = errorMessage;
        this.matchId = matchId;
        this.matchState = matchState;
        this.matchTime = matchTime;
        this.numberOfTurns = numberOfTurns;
        this.playerBlackName = playerBlackName;
        this.playerWhiteName = playerWhiteName;
        this.playTime = playTime;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchState() {
        return matchState;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    public String getPlayerBlackName() {
        return playerBlackName;
    }

    public String getPlayerWhiteName() {
        return playerWhiteName;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setMatchState(String matchState) {
        this.matchState = matchState;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public void setNumberOfTurns(int numberOfTurns) {
        this.numberOfTurns = numberOfTurns;
    }

    public void setPlayerBlackName(String playerBlackId) {
        this.playerBlackName = playerBlackId;
    }

    public void setPlayerWhiteName(String playerWhiteId) {
        this.playerWhiteName = playerWhiteId;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
