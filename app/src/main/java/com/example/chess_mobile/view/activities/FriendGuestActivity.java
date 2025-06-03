package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CancelMatchRequest;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.helper.GsonConfig;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.services.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Duration;
import java.util.Objects;

public class FriendGuestActivity extends Activity implements OnErrorWebSocket {
    private TextView matchId;
    private TextView whiteName;
    private TextView blackName;
    private Button leaveButton;
    private MatchResponse currentMatchResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_guest);
        bindView();
        LoadCurrentMatch();
        setUpLeaveButton();
        reSubscribe();

    }
    public void reSubscribe() {
        Log.d("R3ESUBSRIBE","rE SUBSCRIBE");
        String matchTopic = String.format(SocketManager.MATCH_TOPIC_TEMPLATE, currentMatchResponse.getMatchId());
        SocketManager.getInstance().subscribeTopic(matchTopic,tMes->{
            Log.d("DESTROY",tMes.getPayload());
            if(tMes.getPayload().equals("destroyed")) {
                new AlertDialog.Builder(this).setTitle("Host leaved").
                        setMessage("Host destroyed room, go back").setCancelable(false)
                        .setPositiveButton("Back", (dialog, i) -> startGameModeSelectionActivity())
                        .show();
            }
        });

        String chessStartTopic =
                SocketManager.CHESS_START_TOPIC_TEMPLATE + '/' + this.currentMatchResponse.getMatchId();
        Log.d("CHESS_START_TOPIC_GUESS",chessStartTopic);
        SocketManager.getInstance().subscribeTopic(chessStartTopic, stompMessage -> {
            Log.d("START_GAME",stompMessage.getPayload());
            if (!Boolean.parseBoolean(
                    Strings.emptyToNull(this.currentMatchResponse.getErrorMessage()))) {
                this.currentMatchResponse = GsonConfig.getInstance().fromJson(stompMessage.getPayload(),
                        MatchResponse.class);
                String matchId = this.currentMatchResponse.getMatchId();
                Duration duration = Duration.ofMinutes(this.currentMatchResponse.getPlayTime());
                EMatch type = EMatch.PRIVATE;

                String currentPlayerId = getUID();
                if (currentPlayerId.isEmpty()) return;
                boolean isMainPlayerWhite =
                        this.currentMatchResponse.getPlayerWhiteId().equals(currentPlayerId);
                PlayerChess whitePlayer = new PlayerChess(this.currentMatchResponse.getPlayerWhiteId(), "White",
                        EPlayer.WHITE);
                PlayerChess blackPlayer = new PlayerChess(this.currentMatchResponse.getPlayerBlackId(), "Black",
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
    public void bindView() {
        this.matchId = findViewById(R.id.fg_match_id);
        this.whiteName =findViewById(R.id.fg_white_name);
        this.blackName = findViewById(R.id.fg_black_name);
        this.leaveButton= findViewById(R.id.btn_leave);
    }
    public void setUpLeaveButton() {
        this.leaveButton.setOnClickListener(v->{
            String userId = getUID();
            if (userId.isEmpty()) return;
            CancelMatchRequest cancelMatchRequest = new CancelMatchRequest(currentMatchResponse.getMatchId(), userId);
            String jsonMessage = GsonConfig.getInstance().toJson(cancelMatchRequest);
            SocketManager.getInstance().sendMessage(jsonMessage,"/app/chess/cancelMatch");
            finish();
        });
    }
    public void LoadCurrentMatch() {
        this.currentMatchResponse = (MatchResponse) this.getIntent().getSerializableExtra("Match_Info");
        if (this.currentMatchResponse==null) {
            new AlertDialog.Builder(this).setTitle("Some thing went wrong").
                    setMessage("Back to the previous screen")
                    .setCancelable(false)
                    .setPositiveButton("Back", (dialogInterface, i) -> {
                        startActivity(new Intent( this,GameModeSelectionActivity.class));
                        finish();
                    }).show();
            return;
        }
        String userId = getUID();
        if (userId.isEmpty()) return;
        String blackID = currentMatchResponse.getPlayerBlackId();
        if(blackID.equals(userId)) {
            whiteName.setText(R.string.host);
            blackName.setText(R.string.you);
        }
        else {
            blackName.setText(R.string.host);
            whiteName.setText(R.string.you);
        }
        String matchTxt = "Match Id: "+ this.currentMatchResponse.getMatchId();
        matchId.setText(matchTxt);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        String chessStartTopic =
                SocketManager.CHESS_START_TOPIC_TEMPLATE + '/' + this.currentMatchResponse.getMatchId();
        String matchTopic = String.format(SocketManager.MATCH_TOPIC_TEMPLATE, currentMatchResponse.getMatchId());
        SocketManager.getInstance().unsubscribeTopic(matchTopic);
        SocketManager.getInstance().unsubscribeTopic(chessStartTopic);
    }
    @Override
    public void OnError() {
        new AlertDialog.Builder(this).setTitle("Connection Error").
                setMessage("Check your wifi connection!").setCancelable(false).setPositiveButton(
                        "Back", (dialog, i) -> {
                            Intent intent = new Intent( this,GameModeSelectionActivity.class);
                            startActivity(intent);
                            finish();
                        }).show();
    }


    private boolean isCurrentUserNonExist() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }
    private String getUID() {
        if (isCurrentUserNonExist()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return  "";
        }
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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
}