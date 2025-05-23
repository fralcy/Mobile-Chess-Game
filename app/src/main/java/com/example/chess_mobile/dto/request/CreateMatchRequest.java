package com.example.chess_mobile.dto.request;

import com.example.chess_mobile.model.logic.moves.EMoveType;
import com.example.chess_mobile.model.match.EMatch;

public class CreateMatchRequest {

    private String playerID;

    private Boolean playAsWhite; // nguoi tao muon chon quan trang hay khong

    private Integer playTime;

    private EMatch matchType;

    public CreateMatchRequest() {
    }

    public CreateMatchRequest(EMatch matchType, Boolean playAsWhite, String playerID, Integer playTime) {
        this.matchType = matchType;
        this.playAsWhite = playAsWhite;
        this.playerID = playerID;
        this.playTime = playTime;
    }
}
