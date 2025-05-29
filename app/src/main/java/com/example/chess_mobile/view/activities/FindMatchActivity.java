package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.websocket.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class FindMatchActivity extends AppCompatActivity implements OnErrorWebSocket {

    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FindMatch();

        SocketManager.getInstance().connect(() -> {
            // Sau khi connect thành công, mới subscribe
            SocketManager.getInstance().subscribeTopic("/user/queue/match",null);
        },this);

        int playTime = getIntent().getIntExtra("Rank_Play_Time",0);
        this.currentUser= FirebaseAuth.getInstance().getCurrentUser();
        CreateMatchRequest request = new CreateMatchRequest(EMatch.RANKED, null,this.currentUser.getUid(),playTime );
        String json = new Gson().toJson(request);
        SocketManager.getInstance().sendMessage(json,SocketManager.beEndPoint+"/app/chess/create");

    }
    public void FindMatch() {
        String textMessage = "Wanna play?";
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.find_match_layout);
        /*((TextView) dialog.findViewById(R.id.dialogMessage)).setText(textMessage);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l-> startChess());
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l-> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();*/
        dialog.findViewById(R.id.back).setOnClickListener(c->{
            Intent intent = new Intent(FindMatchActivity.this, RankedMatchActivity.class);
            startActivity(intent);

            finish();
        });
        dialog.show();
    }
    public void registerToFindMatchQueue() {

    }

    public void setUpListener() {
        
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        SocketManager.getInstance().disconnect();

    }

    @Override
    public void OnError() {

    }
}