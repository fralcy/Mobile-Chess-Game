package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.websocket.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class FindMatchActivity extends AppCompatActivity implements OnErrorWebSocket {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FindMatch();

        SocketManager.getInstance().connect(() -> {
            // Sau khi connect thành công, mới subscribe
            SocketManager.getInstance().subscribeTopic("user/queue/match",tMessage->{
                Log.d("RANK",tMessage.getPayload());
            });
        },this);

        int playTime = getIntent().getIntExtra("Rank_Play_Time",0);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (getUID().isEmpty()) return;
        CreateMatchRequest request = new CreateMatchRequest(EMatch.RANKED, null, getUID(),playTime );
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);
        SocketManager.getInstance().sendMessage(json,"/app/chess/create");

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