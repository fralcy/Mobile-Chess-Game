package com.example.chess_mobile.view.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;

public class FriendGuestActivity extends AppCompatActivity {
    private TextView matchId;
    private TextView whiteName;
    private TextView blackName;
    private Button leaveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_guest);
        bindView();

    }
    public void bindView() {
        this.matchId = findViewById(R.id.fg_match_id);
        this.whiteName =findViewById(R.id.fg_white_name);
        this.blackName = findViewById(R.id.fg_black_name);
        this.leaveButton= findViewById(R.id.btn_leave);
    }
    public void connectAndSetUpListener() {}
}