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
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.model.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.Objects;

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
        setButtonListener();
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
            this.startButton.setVisibility(View.VISIBLE);
            this.progressBar.setVisibility(View.GONE);
            titleLobby.setText(R.string.opponent_joined_your_room);
            if(this.isBlack) {
                whiteName.setText(R.string.guest);
            }
            else {
                blackName.setText(R.string.guest);
            }
        }

        else {
            this.startButton.setVisibility(View.GONE);
            this.progressBar.setVisibility(View.VISIBLE);
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
        new AlertDialog.Builder(this).setTitle("Connection Error").
                setMessage("Check your wifi connection! ").setCancelable(false).setPositiveButton(
                        "Back", (dialog, i) -> {
                            Intent intent = new Intent( this,GameModeSelectionActivity.class);
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
        this.isBlack = this.currentMatch.getPlayerBlackId()!=null;
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
    public void setButtonListener() {
        this.backButton.setOnClickListener(v->{
            SocketManager.getInstance().sendMessage(null,"/app/chess/destroyMatch/"+this.currentMatch.getMatchId());
            startGameModeSelectionActivity();
        });

        this.startButton.setOnClickListener(v -> {
            String matchId = this.currentMatch.getMatchId();
            Duration duration = Duration.ofMinutes(this.currentMatch.getPlayTime());
            EMatch type = EMatch.PRIVATE;

            String currentPlayerId = getUID();
            if (currentPlayerId.isEmpty()) return;
            boolean isMainPlayerWhite =
                    this.currentMatch.getPlayerWhiteId().equals(currentPlayerId);
            Player whitePlayer = new Player(this.currentMatch.getPlayerWhiteId(), "White",
                    EPlayer.WHITE);
            Player blackPlayer = new Player(this.currentMatch.getPlayerBlackId(), "Black",
                    EPlayer.BLACK);
            Player mainPlayer = isMainPlayerWhite ? whitePlayer : blackPlayer;
            Player opponent = isMainPlayerWhite ? blackPlayer : whitePlayer;
            startChessRoomActivity(mainPlayer, opponent, duration, matchId, type);
        });
    }

    private void startChessRoomActivity(Player mainPlayer, Player opponent, Duration duration,
                                        String matchId, EMatch type) {
        Intent roomChessIntent = new Intent(this, RoomChessActivity.class);
        roomChessIntent.putExtra(RoomChessActivity.MAIN_PLAYER, mainPlayer);
        roomChessIntent.putExtra(RoomChessActivity.OPPONENT_PLAYER, opponent);
        roomChessIntent.putExtra(RoomChessActivity.DURATION, duration);
        roomChessIntent.putExtra(RoomChessActivity.MATCH_ID, matchId);
        roomChessIntent.putExtra(RoomChessActivity.TYPE, type);
        roomChessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(roomChessIntent);
        finish();
    }

    private void startGameModeSelectionActivity() {
        Intent intent = new Intent(this, GameModeSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().unsubscribeTopic("/topic/match/"+currentMatch.getMatchId());

    }

    private boolean isCurrentUserNonExist() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }
    private String getUID() {
        if (isCurrentUserNonExist()) {
            String destroyMatchTopic =
                    String.format(SocketManager.CHESS_DESTROY_MATCH_TOPIC_TEMPLATE,
                            this.currentMatch.getMatchId());
            SocketManager.getInstance().sendMessage(null, destroyMatchTopic);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return  "";
        }
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}