package com.example.chess_mobile.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.model.websocket.SocketManager;

public class FriendMatchLobbyActivity extends AppCompatActivity implements OnErrorWebSocket {
    private MatchResponse currentMatch;
    private TextView blackName;
    private TextView whiteName;
    private Button backButton;
    private TextView matchIdText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_match_lobby);
        bindView();
        loadPlayer();
        setUpBackButton();
        SocketManager.getInstance().subscribeTopic("/topic/match/"+currentMatch.getMatchId(),tMes->{

        });

    }

    @Override
    public void OnError() {

    }
    public void bindView() {
        this.blackName = findViewById(R.id.tv_black_name);
        this.whiteName = findViewById(R.id.tv_white_name);
        this.backButton = findViewById(R.id.btn_cancel);
        this.matchIdText = findViewById(R.id.tv_match_id);
    }
    public void loadPlayer() {
        this.currentMatch = (MatchResponse) getIntent().getSerializableExtra("Match_Info");
        boolean isBlack =this.currentMatch.getPlayerBlackId()!=null;
        if(isBlack) {
            blackName.setText("You");
            whiteName.setText("Waiting ...");
        }
        else {
            blackName.setText("Waiting ...");
            whiteName.setText("You");
        }
        this.matchIdText.setText("Match Id: "+this.currentMatch.getMatchId());



    }
    public void setUpBackButton() {
        this.backButton.setOnClickListener(v->{
            Intent intent = new Intent(this, GameModeSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}