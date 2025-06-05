package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.dto.request.MoveRequest;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.helper.GsonConfig;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.services.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.Objects;

public class FindMatchActivity extends AppCompatActivity implements OnErrorWebSocket {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        FindMatch();

        SocketManager.getInstance().connect(() -> {
            // Sau khi connect thành công, mới subscribe
            SocketManager.getInstance().subscribeTopic("/topic/rank-match/"+FirebaseAuth.getInstance().getCurrentUser().getUid(),tMes->{
                Log.d("RANK",tMes.getPayload());
                Gson gson = new Gson();
                String tMesString = tMes.getPayload();
                if(tMesString.charAt(0)!='{') {
                    return;
                }
                MatchResponse matchResponse = gson.fromJson(tMes.getPayload(),MatchResponse.class);
                String currentPlayerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(currentPlayerId.equals(matchResponse.getPlayerBlackId())) {
                    SocketManager.getInstance().sendMessage(matchResponse.getMatchId(),SocketManager.CHESS_START_APP_TEMPLATE);
                    PlayerChess mainPlayer = new PlayerChess(currentPlayerId,"Black", EPlayer.BLACK);
                    PlayerChess opponentPlayer = new PlayerChess(matchResponse.getPlayerBlackId(),"White",EPlayer.WHITE);
                    Duration duration = Duration.ofMinutes(matchResponse.getPlayTime());
                    EMatch eMatch = EMatch.RANKED;
                    startChessRoomActivity(mainPlayer,opponentPlayer,duration,matchResponse.getMatchId(),eMatch);

                }
                else {
                    String chessStartTopic =
                            SocketManager.CHESS_START_TOPIC_TEMPLATE + '/' + matchResponse.getMatchId();
                    SocketManager.getInstance().subscribeTopic(chessStartTopic,tMessage->{
                        Log.d("START GAME SIGNAL",tMessage.getPayload());


                        PlayerChess mainPlayer = new PlayerChess(currentPlayerId,"White", EPlayer.WHITE);
                        PlayerChess opponentPlayer = new PlayerChess(matchResponse.getPlayerBlackId(),"Black",EPlayer.BLACK);
                        Duration duration = Duration.ofMinutes(matchResponse.getPlayTime());
                        EMatch eMatch = EMatch.RANKED;
                        startChessRoomActivity(mainPlayer,opponentPlayer,duration,matchResponse.getMatchId(),eMatch);
                    });
                }


            });
            int playTime = getIntent().getIntExtra("Rank_Play_Time",0);

            if (getUID().isEmpty()) return;
            CreateMatchRequest request = new CreateMatchRequest(EMatch.RANKED, null, getUID(),playTime );
            String json = GsonConfig.getInstance().toJson(request);
            SocketManager.getInstance().sendMessage(json,"/app/chess/create");
            SocketManager.getInstance().subscribeTopic("/user/queue/match/error",tMes->{
                Log.d("RANK_ERROR",tMes.getPayload());
            });
        },this);

    }
    public void startChessRoomActivity(PlayerChess mainPlayer, PlayerChess opponent, Duration duration,
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
    public void FindMatch() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.find_match_layout);
        dialog.findViewById(R.id.back).setOnClickListener(c->{
            Intent intent = new Intent(FindMatchActivity.this, RankedMatchActivity.class);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //SocketManager.getInstance().disconnect();

    }

    @Override
    public void OnError() {

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
}