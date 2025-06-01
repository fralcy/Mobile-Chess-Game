package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CancelMatchRequest;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.model.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

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
        String matchTopic = String.format(SocketManager.MATCH_TOPIC_TEMPLATE, currentMatchResponse.getMatchId());
        SocketManager.getInstance().subscribeTopic(matchTopic,tMes->{
            Log.d("DESTROY",tMes.getPayload());
            if(tMes.getPayload().equals("destroyed")) {
                new AlertDialog.Builder(FriendGuestActivity.this).setTitle("Host leaved").
                        setMessage("Host destroyed room, go back").setCancelable(false).setPositiveButton(
                                "Back", (dialog, i) -> {
                                    Intent intent = new Intent( FriendGuestActivity.this,GameModeSelectionActivity.class);
                                    startActivity(intent);
                                    finish();
                                }).show();
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
            String jsonMessage = new Gson().toJson(cancelMatchRequest);
            SocketManager.getInstance().sendMessage(jsonMessage,"/app/chess/cancelMatch");
            finish();
        });
    }
    public void LoadCurrentMatch() {
        this.currentMatchResponse = (MatchResponse) this.getIntent().getSerializableExtra("Match_Info");
        if (this.currentMatchResponse==null) {
            new AlertDialog.Builder(FriendGuestActivity.this).setTitle("Some thing went wrong").
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
        String matchTopic = String.format(SocketManager.MATCH_TOPIC_TEMPLATE, currentMatchResponse.getMatchId());
        SocketManager.getInstance().unsubscribeTopic(matchTopic);
    }
    @Override
    public void OnError() {
        new AlertDialog.Builder(FriendGuestActivity.this).setTitle("Connection Error").
                setMessage("Check your wifi connection!").setCancelable(false).setPositiveButton(
                        "Back", (dialog, i) -> {
                            Intent intent = new Intent( FriendGuestActivity.this,GameModeSelectionActivity.class);
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
}