package com.example.chess_mobile.dto.response;

import com.example.chess_mobile.model.match.EMatch;

import java.io.Serializable;
import java.util.Date;

public class MatchResponse implements Serializable {

    private String matchId;
    private String playerWhiteId;
    private String playerBlackId;
    private String matchState;
    private int numberOfTurns;
    private int playTime;
    private Date matchTime;
    private String ErrorMessage;

    public MatchResponse() {

    }

    public MatchResponse(String errorMessage, String matchId, String matchState, Date matchTime, int numberOfTurns, String playerBlackId, String playerWhiteId, int playTime) {
        ErrorMessage = errorMessage;
        this.matchId = matchId;
        this.matchState = matchState;
        this.matchTime = matchTime;
        this.numberOfTurns = numberOfTurns;
        this.playerBlackId = playerBlackId;
        this.playerWhiteId = playerWhiteId;
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

    public String getPlayerBlackId() {
        return playerBlackId;
    }

    public String getPlayerWhiteId() {
        return playerWhiteId;
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

    public void setPlayerBlackId(String playerBlackId) {
        this.playerBlackId = playerBlackId;
    }

    public void setPlayerWhiteId(String playerWhiteId) {
        this.playerWhiteId = playerWhiteId;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
