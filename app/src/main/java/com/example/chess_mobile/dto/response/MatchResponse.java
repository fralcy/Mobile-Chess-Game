package com.example.chess_mobile.dto.response;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.services.http.HttpClient;
import com.example.chess_mobile.view.activities.InfoActivity;
import com.example.chess_mobile.view.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    public MatchResponseWithPlayerName getPlayerNameFromMatchResponse( ) {

        HttpClient httpClient = new HttpClient();
        String urlWhite = HttpClient.BASE_URL+"player/"+playerWhiteId;
        String urlBlack = HttpClient.BASE_URL+"player/"+playerBlackId;
        MatchResponseWithPlayerName res = new MatchResponseWithPlayerName(this.ErrorMessage, this.matchId, this.matchState,this.matchTime,this.numberOfTurns,"","",this.playTime);
        fetchPlayer(urlWhite,httpClient,res,true);
        fetchPlayer(urlBlack,httpClient,res,false);
        return res;
    }
    public void fetchPlayer(String url, HttpClient httpClient, MatchResponseWithPlayerName res, boolean isFetchWhite) {
        Callback callback= new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    Gson gson = new Gson();
                    String responseJSon= response.body().string();
                    Player player = gson.fromJson(responseJSon, Player.class);
                    if(isFetchWhite) {
                        res.setPlayerWhiteName(player.getPlayerName());
                    }
                    else {
                        res.setPlayerBlackName(player.getPlayerName());
                    }
                }
            }
        };
        httpClient.get(url,null,callback);

    }
}
