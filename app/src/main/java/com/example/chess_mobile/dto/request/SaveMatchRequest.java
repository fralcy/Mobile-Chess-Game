package com.example.chess_mobile.dto.request;

import com.example.chess_mobile.model.match.EMatch;

public class SaveMatchRequest {
    private String matchId;
    private String result;
    private String type;

    public SaveMatchRequest() {

    }
    public SaveMatchRequest(String matchId, String result, String type) {
        this.matchId=matchId;
        this.result=result;
        this.type=type;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
