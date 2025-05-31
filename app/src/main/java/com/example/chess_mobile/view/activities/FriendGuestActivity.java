package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CancelMatchRequest;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.model.websocket.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

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
        SocketManager.getInstance().subscribeTopic("/topic/match/"+currentMatchResponse.getMatchId(),tMes->{
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
            CancelMatchRequest cancelMatchRequest = new CancelMatchRequest(currentMatchResponse.getMatchId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            String jsonMessage = new Gson().toJson(cancelMatchRequest);
            SocketManager.getInstance().sendMessage(jsonMessage,"/app/chess/cancelMatch");
            finish();
        });
    }
    public void LoadCurrentMatch() {
        this.currentMatchResponse = (MatchResponse) this.getIntent().getSerializableExtra("Match_Info");
        if(this.currentMatchResponse==null) {
            new AlertDialog.Builder(FriendGuestActivity.this).setTitle("Some thing went wrong").
                    setMessage("Back to the previous screen").setCancelable(false).setPositiveButton("Back", (dialogInterface, i) -> {
                        Intent intent = new Intent( FriendGuestActivity.this,GameModeSelectionActivity.class);
                        startActivity(intent);
                        finish();
                    }).show();
        }
        if(currentMatchResponse.getPlayerBlackId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            whiteName.setText("Host");
            blackName.setText("You");
        }
        else {
            blackName.setText("Host");
            whiteName.setText("You");
        }
        matchId.setText("Match Id: "+this.currentMatchResponse.getMatchId());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().unsubscribeTopic("/topic/match/"+currentMatchResponse.getMatchId());
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
}