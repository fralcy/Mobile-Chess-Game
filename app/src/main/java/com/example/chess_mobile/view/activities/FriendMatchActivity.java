package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.dto.request.JoinMatchRequest;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.websocket.SocketManager;
import com.example.chess_mobile.view_model.IFriendMatchViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class FriendMatchActivity extends Activity implements IFriendMatchViewModel,OnErrorWebSocket {
    private boolean isWhite;
    private Integer playTime;

    private FirebaseUser currentUser;
    private MatchResponse currentMatch;

    //View
    private Button whiteButton;
    private Button blackButton;
    private Button timeButton10;
    private Button timeButton15;
    private Button timeButton20;
    private EditText customMin;
    private EditText customSec;
    private Button createButton;
    private Button joinButton;
    private EditText roomIdInput;
    private TextView roomIdText;
    private TextView waitingText;

    public Button getBlackButton() {
        return blackButton;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public EditText getCustomMin() {
        return customMin;
    }

    public EditText getCustomSec() {
        return customSec;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Button getJoinButton() {
        return joinButton;
    }

    public Integer getPlayTime() {
        return playTime;
    }

    public EditText getRoomIdInput() {
        return roomIdInput;
    }

    public TextView getRoomIdText() {
        return roomIdText;
    }



    public Button getTimeButton10() {
        return timeButton10;
    }

    public Button getTimeButton15() {
        return timeButton15;
    }

    public Button getTimeButton20() {
        return timeButton20;
    }

    public TextView getWaitingText() {
        return waitingText;
    }

    public Button getWhiteButton() {
        return whiteButton;
    }

    @Override
    public void onStart() {

        super.onStart();
        this.isWhite=true;
        this.playTime = 10;
    }
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_friend_match);
        bindView();
        setUpOnClickListener();

        SocketManager.getInstance().connect(() -> {
            // Sau khi connect thành công, mới subscribe
            SocketManager.getInstance().subscribeTopic("/user/queue/match",topicMessage->{
                String payload = topicMessage.getPayload();
                Gson gson = new Gson();
                currentMatch = gson.fromJson(payload, MatchResponse.class);


                Intent intent = new Intent(FriendMatchActivity.this, FriendMatchLobbyActivity.class);
                intent.putExtra("Match_Info",currentMatch);
                startActivity(intent);
            });
        },this);

        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SocketManager.getInstance().disconnect();
    }
    public void bindView() {
        this.whiteButton = findViewById(R.id.friendMatchButtonWhite);
        this.blackButton = findViewById(R.id.friendMatchButtonBlack);
        this.timeButton10 = findViewById(R.id.friendMatchButton10Min);
        this.timeButton15 = findViewById(R.id.friendMatchButton15Min);
        this.timeButton20= findViewById(R.id.friendMatchButton20Min);
        this.customMin = findViewById(R.id.friendMatchEditTextMinutes);
        this.customSec = findViewById(R.id.friendMatchEditTextSeconds);
        this.createButton= findViewById(R.id.friendMatchButtonCreate);
        this.roomIdInput = findViewById(R.id.friendMatchEditTextRoomId);
        this.joinButton= findViewById(R.id.friendMatchButtonJoin);
        this.roomIdText= findViewById(R.id.friendMatchTextRoomCreated);
        this.waitingText= findViewById(R.id.friendMatchTextWaiting);
    }
    public void setUpOnClickListener() {
        this.whiteButton.setOnClickListener(v->{
            FriendMatchActivity.this.getIntent().putExtra("Host_White",true);
            FriendMatchActivity.this.isWhite=true;
            FriendMatchActivity.this.whiteButton.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this,R.drawable.rounded_button_bg));
            FriendMatchActivity.this.blackButton.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_gray_button_bg));
        });
        this.blackButton.setOnClickListener(v->{
            FriendMatchActivity.this.getIntent().putExtra("Host_White",false);
            FriendMatchActivity.this.isWhite=false;
            FriendMatchActivity.this.whiteButton.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this,R.drawable.rounded_gray_button_bg));
            FriendMatchActivity.this.blackButton.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_button_bg));

        });
        this.timeButton10.setOnClickListener(v->{
            FriendMatchActivity.this.getIntent().putExtra("Friend_Play_Time",10);
            FriendMatchActivity.this.playTime=10;
            FriendMatchActivity.this.timeButton10.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_button_bg));
            FriendMatchActivity.this.timeButton15.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this,R.drawable.rounded_gray_button_bg));
            FriendMatchActivity.this.timeButton20.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_gray_button_bg));
        });
        this.timeButton15.setOnClickListener(v->{
            FriendMatchActivity.this.getIntent().putExtra("Friend_Play_Time",15);
            FriendMatchActivity.this.playTime=15;
            FriendMatchActivity.this.timeButton10.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_gray_button_bg));
            FriendMatchActivity.this.timeButton15.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this,R.drawable.rounded_button_bg));
            FriendMatchActivity.this.timeButton20.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_gray_button_bg));

        });
        this.timeButton20.setOnClickListener(v->{
            FriendMatchActivity.this.getIntent().putExtra("Friend_Play_Time",20);
            FriendMatchActivity.this.playTime=20;
            FriendMatchActivity.this.timeButton10.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_gray_button_bg));
            FriendMatchActivity.this.timeButton15.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this,R.drawable.rounded_gray_button_bg));
            FriendMatchActivity.this.timeButton20.setBackground(ContextCompat.getDrawable(FriendMatchActivity.this, R.drawable.rounded_button_bg));
        });
        this.createButton.setOnClickListener(v->{
            CreateMatchRequest createFriendMatchRequest = new CreateMatchRequest(EMatch.PRIVATE,FriendMatchActivity.this.isWhite,FriendMatchActivity.this.currentUser.getUid(),FriendMatchActivity.this.playTime);
            String json = new Gson().toJson(createFriendMatchRequest);
            SocketManager.getInstance().sendMessage(json, "/app/chess/create");

        });
        this.joinButton.setOnClickListener(v->{
            String matchId = String.valueOf(roomIdInput.getText());
            FriendMatchActivity.this.getIntent().putExtra("match_id",matchId);
            SocketManager.getInstance().subscribeTopic("/topic/match/"+matchId,topicMessage->{
                Log.d("RESPONSE FROM SERVER", topicMessage.getPayload());

            });
            SocketManager.getInstance().subscribeTopic("/topic/match/"+matchId+"/error",topicMessage->{
                Log.d("ERROR FROM SERVER", topicMessage.getPayload());
                String errorMessage;
                if(topicMessage.getPayload().equals("Cannot join this match")) {
                    errorMessage= "You can not join this match";
                }
                else {
                    errorMessage = "The match that you are finding does not exist";
                }
                new AlertDialog.Builder(FriendMatchActivity.this).setTitle(topicMessage.getPayload()).setMessage(errorMessage).setCancelable(false).setPositiveButton("OK",(dialog,which)->{
                    dialog.dismiss();
                }).show();
            });
            JoinMatchRequest joinMatchRequest = new JoinMatchRequest(FriendMatchActivity.this.currentUser.getUid());
            String json = new Gson().toJson(joinMatchRequest);
            SocketManager.getInstance().sendMessage(json,"/app/chess/join/"+matchId);
        });
    }

    @Override
    public void OnError() {
        new AlertDialog.Builder(FriendMatchActivity.this).setTitle("Connection Error").
                setMessage("Check your wifi connection!").setCancelable(false).setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent( FriendMatchActivity.this,GameModeSelectionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().disconnect();
    }
}
