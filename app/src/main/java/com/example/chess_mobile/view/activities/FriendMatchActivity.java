package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.dto.request.JoinMatchRequest;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.websocket.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.example.chess_mobile.view_model.interfaces.IFriendMatchViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.Objects;

public class FriendMatchActivity extends Activity implements IFriendMatchViewModel,
        OnErrorWebSocket {
    private boolean isWhite;
    private Integer playTime;

    private MatchResponse currentMatch;

    //View
    private Button whiteButton;
    private Button blackButton;
    private Button timeButton10;
    private Button timeButton15;
    private Button timeButton20;
    private Button createButton;
    private Button joinButton;
    private EditText roomIdInput;

    public boolean isWhite() {
        return isWhite;
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
            SocketManager.getInstance().subscribeTopic("/user/queue/match",topicMessage-> {
                String payload = topicMessage.getPayload();
                Log.d("FriendMatchActivity_SUCCESS_CONNECTION", topicMessage.getPayload());
                Gson gson = new Gson();
                currentMatch = gson.fromJson(payload, MatchResponse.class);
                Intent intent = new Intent(FriendMatchActivity.this, FriendMatchLobbyActivity.class);
                intent.putExtra("Match_Info",currentMatch);
                FriendMatchActivity.this.getIntent().putExtra("Match_Info",currentMatch);
                startActivity(intent);
                finish();
            });
        },this);

        if (isCurrentUserNonExist()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SocketManager.getInstance().disconnect();
    }
    public void bindView() {
//        EditText customMin = findViewById(R.id.friendMatchEditTextMinutes);
//        EditText customSec = findViewById(R.id.friendMatchEditTextSeconds);
//        TextView roomIdText = findViewById(R.id.friendMatchTextRoomCreated);
//        TextView waitingText = findViewById(R.id.friendMatchTextWaiting);
        this.whiteButton = findViewById(R.id.friendMatchButtonWhite);
        this.blackButton = findViewById(R.id.friendMatchButtonBlack);
        this.timeButton10 = findViewById(R.id.friendMatchButton10Min);
        this.timeButton15 = findViewById(R.id.friendMatchButton15Min);
        this.timeButton20= findViewById(R.id.friendMatchButton20Min);
        this.createButton= findViewById(R.id.friendMatchButtonCreate);
        this.roomIdInput = findViewById(R.id.friendMatchEditTextRoomId);
        this.joinButton= findViewById(R.id.friendMatchButtonJoin);
    }
    public void setUpOnClickListener() {
        this.whiteButton.setOnClickListener(v->{
            this.getIntent().putExtra("Host_White",true);
            this.isWhite=true;
            this.whiteButton.setBackground(ContextCompat.getDrawable(this,R.drawable.rounded_button_bg));
            this.blackButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));
        });
        this.blackButton.setOnClickListener(v->{
            this.getIntent().putExtra("Host_White",false);
            this.isWhite=false;
            this.whiteButton.setBackground(ContextCompat.getDrawable(this,R.drawable.rounded_gray_button_bg));
            this.blackButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_bg));

        });
        this.timeButton10.setOnClickListener(v->{
            this.getIntent().putExtra("Friend_Play_Time",10);
            this.playTime=10;
            this.timeButton10.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_bg));
            this.timeButton15.setBackground(ContextCompat.getDrawable(this,R.drawable.rounded_gray_button_bg));
            this.timeButton20.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));
        });
        this.timeButton15.setOnClickListener(v->{
            this.getIntent().putExtra("Friend_Play_Time",15);
            this.playTime=15;
            this.timeButton10.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));
            this.timeButton15.setBackground(ContextCompat.getDrawable(this,R.drawable.rounded_button_bg));
            this.timeButton20.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));

        });
        this.timeButton20.setOnClickListener(v->{
            this.getIntent().putExtra("Friend_Play_Time",20);
            this.playTime=20;
            this.timeButton10.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));
            this.timeButton15.setBackground(ContextCompat.getDrawable(this,R.drawable.rounded_gray_button_bg));
            this.timeButton20.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_bg));
        });
        this.createButton.setOnClickListener(v->{
            CreateMatchRequest createFriendMatchRequest = new CreateMatchRequest( EMatch.PRIVATE,
                    this.isWhite,getUID(),this.playTime);
            String json = new Gson().toJson(createFriendMatchRequest);
            SocketManager.getInstance().sendMessage(json, "/app/chess/create");

        });
        this.joinButton.setOnClickListener(v->{
            String matchId = String.valueOf(roomIdInput.getText());
            this.getIntent().putExtra("match_id",matchId);
            SocketManager.getInstance().subscribeTopic("/topic/match/"+matchId,topicMessage->{
                Log.d("RESPONSE FROM SERVER", topicMessage.getPayload());
                MatchResponse matchResponse = new Gson().fromJson(topicMessage.getPayload(),MatchResponse.class);

                SocketManager.getInstance().unsubscribeTopic("/topic/match/"+matchId);
                Intent intent  = new Intent(this, FriendGuestActivity.class);
                intent.putExtra("Match_Info", matchResponse);

                startActivity(intent);
                finish();
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
                new AlertDialog.Builder(this).
                        setTitle(topicMessage.getPayload())
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK",(dialog,which)->dialog.dismiss()).show();
            });

            if (getUID().isEmpty()) return;

            JoinMatchRequest joinMatchRequest = new JoinMatchRequest(getUID());
            String json = new Gson().toJson(joinMatchRequest);
            SocketManager.getInstance().sendMessage(json,"/app/chess/join/"+matchId);
        });

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().disconnect();
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
