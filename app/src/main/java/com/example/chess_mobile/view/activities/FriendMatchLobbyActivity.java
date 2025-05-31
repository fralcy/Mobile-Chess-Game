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
import com.example.chess_mobile.model.websocket.SocketManager;
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
        SocketManager.getInstance().subscribeTopic("/topic/match/"+currentMatch.getMatchId(),tMes->{
            Log.d("JOIN", tMes.getPayload());
            displayBaseOntMes(tMes.getPayload());
        });
        SocketManager.getInstance().subscribeTopic("/topic/match/"+currentMatch.getMatchId()+"/error",tMes->{
            Log.d("ERROR", tMes.getPayload());
        });

    }
    public void displayBaseOntMes(String tMes) {
        Gson gson = new Gson();
        MatchResponse matchResponse = gson.fromJson(tMes,MatchResponse.class);
        if(matchResponse.getPlayerBlackId()!=null&&matchResponse.getPlayerWhiteId()!=null) {
            FriendMatchLobbyActivity.this.startButton.setVisibility(View.VISIBLE);
            FriendMatchLobbyActivity.this.progressBar.setVisibility(View.GONE);
            titleLobby.setText("Opponent joined your room");
            if(this.isBlack) {
                whiteName.setText("Guest");
            }
            else {
                blackName.setText("Guest");
            }
        }

        else {
            FriendMatchLobbyActivity.this.startButton.setVisibility(View.GONE);
            FriendMatchLobbyActivity.this.progressBar.setVisibility(View.VISIBLE);
            titleLobby.setText("Waiting for Opponent");
            if(this.isBlack) {
                whiteName.setText("Waiting....");
            }
            else {
                blackName.setText("Waiting....");
            }
        }
    }
    @Override
    public void OnError() {
        new AlertDialog.Builder(FriendMatchLobbyActivity.this).setTitle("Connection Error").
                setMessage("Check your wifi connection!").setCancelable(false).setPositiveButton(
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

        this.isBlack =this.currentMatch.getPlayerBlackId()!=null;
        if(this.isBlack) {
            blackName.setText("You");
            whiteName.setText("Waiting ...");
        }
        else {
            blackName.setText("Waiting ...");
            whiteName.setText("You");
        }
        this.matchIdText.setText("Match Id: "+this.currentMatch.getMatchId());



    }
    public void setupStartButton() {

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