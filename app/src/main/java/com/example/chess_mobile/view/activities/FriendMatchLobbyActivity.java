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
import android.widget.Toast;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.helper.GsonConfig;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.services.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.android.gms.common.util.Strings;
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

        String chessStartTopic =
                SocketManager.CHESS_START_TOPIC_TEMPLATE + '/' + this.currentMatch.getMatchId();
        Log.d("CHESS_START_TOPIC_LOBBY",chessStartTopic);
        SocketManager.getInstance().subscribeTopic(chessStartTopic, stompMessage -> {
            MatchResponse matchResponse = GsonConfig.getInstance().fromJson(stompMessage.getPayload(),MatchResponse.class);
            Log.d("START_GAME",stompMessage.getPayload());
            if (!Boolean.parseBoolean(
                    Strings.emptyToNull(matchResponse.getErrorMessage()))) {
                String matchId = matchResponse.getMatchId();
                Duration duration = Duration.ofMinutes(matchResponse.getPlayTime());
                EMatch type = EMatch.PRIVATE;

                String currentPlayerId = getUID();
                if (currentPlayerId.isEmpty()) return;
                boolean isMainPlayerWhite =
                        matchResponse.getPlayerWhiteId().equals(currentPlayerId);
                PlayerChess whitePlayer = new PlayerChess(matchResponse.getPlayerWhiteId(), "White",
                        EPlayer.WHITE);
                PlayerChess blackPlayer = new PlayerChess(matchResponse.getPlayerBlackId(), "Black",
                        EPlayer.BLACK);
                PlayerChess mainPlayer = isMainPlayerWhite ? whitePlayer : blackPlayer;
                PlayerChess opponent = isMainPlayerWhite ? blackPlayer : whitePlayer;
                startChessRoomActivity(mainPlayer, opponent, duration, matchId, type);
            } else {
                startGameModeSelectionActivity();
                Toast.makeText(this, "Room cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void displayBaseOntMes(String tMes) {
        Gson gson = GsonConfig.getInstance();
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
            SocketManager.getInstance()
                    .sendMessage(this.currentMatch.getMatchId(),
                            SocketManager.CHESS_START_APP_TEMPLATE);
        });
    }

    private void startChessRoomActivity(
            PlayerChess mainPlayer, PlayerChess opponent, Duration duration,
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
        String chessStartTopic =
                SocketManager.CHESS_START_TOPIC_TEMPLATE + '/' + this.currentMatch.getMatchId();
        SocketManager.getInstance().unsubscribeTopic(chessStartTopic);
    }

    private boolean isCurrentUserNonExist() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }
    private String getUID() {
        if (isCurrentUserNonExist()) {
            String destroyMatchTopic =
                    String.format(SocketManager.CHESS_DESTROY_MATCH_APP_TEMPLATE,
                            this.currentMatch.getMatchId());
            SocketManager.getInstance().sendMessage(null, destroyMatchTopic);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return  "";
        }
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}