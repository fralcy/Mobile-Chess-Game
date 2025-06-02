package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.CreateMatchRequest;
import com.example.chess_mobile.helper.GsonConfig;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.services.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;

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