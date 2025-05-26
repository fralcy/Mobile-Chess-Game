package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.websocket.SocketManager;
import com.example.chess_mobile.view_model.IFriendMatchViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class FriendMatchActivity extends Activity implements IFriendMatchViewModel {
    private boolean isWhite;
    private Integer playTime;
    private SocketManager socketManager;
    private FirebaseUser currentUser;

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
        this.socketManager = new SocketManager();
        this.socketManager.connect(() -> {
            // Sau khi connect thành công, mới subscribe
            this.socketManager.subscribeTopic("/user/queue/match");
        });

        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.socketManager.disconnect();
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
            FriendMatchActivity.this.socketManager.sendMessage(json, "/app/chess/create");

        });
    }

}
