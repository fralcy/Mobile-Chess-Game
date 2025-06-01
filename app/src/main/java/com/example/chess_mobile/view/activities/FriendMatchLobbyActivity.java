package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.model.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.gson.Gson;

public class FriendMatchLobbyActivity extends Activity implements OnErrorWebSocket {
    private MatchResponse currentMatch;
    private TextView blackName;
    private TextView whiteName;
    private Button backButton;
    private TextView matchIdText;

    private Button startButton;
    private ProgressBar progressBar;
    private TextView titleLobby;
    private boolean isBlack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_match_lobby);
        bindView();
        loadPlayer();
        setUpBackButton();
        String matchTopic = String.format(SocketManager.MATCH_TOPIC_TEMPLATE, currentMatch.getMatchId());
        String errorTopic = String.format(SocketManager.MATCH_ERROR_TOPIC_TEMPLATE, currentMatch.getMatchId());
        SocketManager.getInstance().subscribeTopic(matchTopic,tMes->{
            Log.d("JOIN", tMes.getPayload());
            displayBaseOntMes(tMes.getPayload());
        });
        SocketManager.getInstance().subscribeTopic(errorTopic,tMes->Log.d("ERROR", tMes.getPayload()));

    }
    public void displayBaseOntMes(String tMes) {
        Gson gson = new Gson();
        MatchResponse matchResponse = gson.fromJson(tMes,MatchResponse.class);
        if(matchResponse.getPlayerBlackId()!=null&&matchResponse.getPlayerWhiteId()!=null) {
            FriendMatchLobbyActivity.this.startButton.setVisibility(View.VISIBLE);
            FriendMatchLobbyActivity.this.progressBar.setVisibility(View.GONE);
            titleLobby.setText(R.string.opponent_joined_your_room);
            if(this.isBlack) {
                whiteName.setText(R.string.guest);
            }
            else {
                blackName.setText(R.string.guest);
            }
        }

        else {
            FriendMatchLobbyActivity.this.startButton.setVisibility(View.GONE);
            FriendMatchLobbyActivity.this.progressBar.setVisibility(View.VISIBLE);
            titleLobby.setText(R.string.waiting_for_opponent);
            if(this.isBlack) {
                whiteName.setText(R.string.waiting);
            }
            else {
                blackName.setText(R.string.waiting);
            }
        }
    }
    @Override
    public void OnError() {
        new AlertDialog.Builder(FriendMatchLobbyActivity.this).setTitle("Connection Error").
                setMessage("Check your wifi connection! ").setCancelable(false).setPositiveButton(
                        "Back", (dialog, i) -> {
                            Intent intent = new Intent( FriendMatchLobbyActivity.this,GameModeSelectionActivity.class);
                            startActivity(intent);
                            finish();
                        }).show();
    }
    public void bindView() {
        this.blackName = findViewById(R.id.tv_black_name);
        this.whiteName = findViewById(R.id.tv_white_name);
        this.backButton = findViewById(R.id.btn_cancel);
        this.matchIdText = findViewById(R.id.tv_match_id);
        this.startButton = findViewById(R.id.btn_start);
        this.progressBar = findViewById(R.id.progress_bar);
        this.titleLobby = findViewById(R.id.tv_waiting_title);
    }
    public void loadPlayer() {
        this.currentMatch = (MatchResponse) getIntent().getSerializableExtra("Match_Info");
        assert currentMatch != null;
        this.isBlack =this.currentMatch.getPlayerBlackId() != null;
        if(this.isBlack) {
            blackName.setText(R.string.you);
            whiteName.setText(R.string.waiting);
        }
        else {
            blackName.setText(R.string.waiting);
            whiteName.setText(R.string.you);
        }
        String matchIdText = "Match Id: "+ this.currentMatch.getMatchId();
        this.matchIdText.setText(matchIdText);



    }
    public void setUpBackButton() {
        this.backButton.setOnClickListener(v->{
            SocketManager.getInstance().sendMessage(null,"/app/chess/destroyMatch/"+this.currentMatch.getMatchId());
            Intent intent = new Intent(this, GameModeSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().unsubscribeTopic("/topic/match/"+currentMatch.getMatchId());

    }

}