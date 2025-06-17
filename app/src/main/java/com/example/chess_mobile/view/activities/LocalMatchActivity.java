package com.example.chess_mobile.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.view_model.interfaces.ILocalMatchViewModel;

import java.time.Duration;

public class LocalMatchActivity extends AppCompatActivity implements ILocalMatchViewModel {
    private Duration whitePlayerTime = Duration.ofMinutes(10);
    private Duration blackPlayerTime = Duration.ofMinutes(10);

    private Button buttonWhite10, buttonWhite15, buttonWhite20;
    private Button buttonBlack10, buttonBlack15, buttonBlack20;
    private Button buttonPlay;
    // Features checkboxes
    private CheckBox checkBoxCriticalHit;
    private CheckBox checkBoxEmotion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_local_match);

        bindViews();
        setupWhiteTimeButtons();
        setupBlackTimeButtons();
        setupPlayButton();

        // Set default selections
        updateWhiteTimeButtons(10);
        updateBlackTimeButtons(10);
    }

    private void bindViews() {
        buttonWhite10 = findViewById(R.id.localMatchWhiteButton10Min);
        buttonWhite15 = findViewById(R.id.localMatchWhiteButton15Min);
        buttonWhite20 = findViewById(R.id.localMatchWhiteButton20Min);

        buttonBlack10 = findViewById(R.id.localMatchBlackButton10Min);
        buttonBlack15 = findViewById(R.id.localMatchBlackButton15Min);
        buttonBlack20 = findViewById(R.id.localMatchBlackButton20Min);

        buttonPlay = findViewById(R.id.localMatchButtonPlay);
        checkBoxCriticalHit = findViewById(R.id.checkBoxCriticalHit);
        checkBoxEmotion = findViewById(R.id.checkBoxEmotion);
    }

    private void setupWhiteTimeButtons() {
        buttonWhite10.setOnClickListener(v -> {
            whitePlayerTime = Duration.ofMinutes(10);
            updateWhiteTimeButtons(10);
            onWhiteTimeSelected(10, this);
        });

        buttonWhite15.setOnClickListener(v -> {
            whitePlayerTime = Duration.ofMinutes(15);
            updateWhiteTimeButtons(15);
            onWhiteTimeSelected(15, this);
        });

        buttonWhite20.setOnClickListener(v -> {
            whitePlayerTime = Duration.ofMinutes(20);
            updateWhiteTimeButtons(20);
            onWhiteTimeSelected(20, this);
        });
    }

    private void setupBlackTimeButtons() {
        buttonBlack10.setOnClickListener(v -> {
            blackPlayerTime = Duration.ofMinutes(10);
            updateBlackTimeButtons(10);
            onBlackTimeSelected(10, this);
        });

        buttonBlack15.setOnClickListener(v -> {
            blackPlayerTime = Duration.ofMinutes(15);
            updateBlackTimeButtons(15);
            onBlackTimeSelected(15, this);
        });

        buttonBlack20.setOnClickListener(v -> {
            blackPlayerTime = Duration.ofMinutes(20);
            updateBlackTimeButtons(20);
            onBlackTimeSelected(20, this);
        });
    }

    private void setupPlayButton() {
        buttonPlay.setOnClickListener(v -> {
            onPlayButtonClicked(this);
            startLocalMatch();
        });
    }

    private void updateWhiteTimeButtons(int selectedMinutes) {
        // Reset all buttons
        buttonWhite10.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        buttonWhite15.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        buttonWhite20.setBackgroundResource(R.drawable.rounded_gray_button_bg);

        // Highlight selected
        switch (selectedMinutes) {
            case 10 -> buttonWhite10.setBackgroundResource(R.drawable.rounded_button_bg);
            case 15 -> buttonWhite15.setBackgroundResource(R.drawable.rounded_button_bg);
            case 20 -> buttonWhite20.setBackgroundResource(R.drawable.rounded_button_bg);
        }
    }

    private void updateBlackTimeButtons(int selectedMinutes) {
        // Reset all buttons
        buttonBlack10.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        buttonBlack15.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        buttonBlack20.setBackgroundResource(R.drawable.rounded_gray_button_bg);

        // Highlight selected
        switch (selectedMinutes) {
            case 10 -> buttonBlack10.setBackgroundResource(R.drawable.rounded_button_bg);
            case 15 -> buttonBlack15.setBackgroundResource(R.drawable.rounded_button_bg);
            case 20 -> buttonBlack20.setBackgroundResource(R.drawable.rounded_button_bg);
        }
    }

    private void startLocalMatch() {
        // Tạo 2 player cho local match
        PlayerChess whitePlayer = new PlayerChess("white_local", "White Player", EPlayer.WHITE);
        PlayerChess blackPlayer = new PlayerChess("black_local", "Black Player", EPlayer.BLACK);

        Intent intent = new Intent(this, RoomChessActivity.class);
        intent.putExtra(RoomChessActivity.MAIN_PLAYER, whitePlayer); // Luôn bắt đầu với White
        intent.putExtra(RoomChessActivity.OPPONENT_PLAYER, blackPlayer);
        intent.putExtra(RoomChessActivity.DURATION, whitePlayerTime); // Sử dụng white time làm duration chính
        intent.putExtra(RoomChessActivity.TYPE, EMatch.LOCAL);
        intent.putExtra("WHITE_TIME", whitePlayerTime);
        intent.putExtra("BLACK_TIME", blackPlayerTime);

        intent.putExtra("CRITICAL_HIT_ENABLED", checkBoxCriticalHit.isChecked());
        intent.putExtra("EMOTION_ENABLED", checkBoxEmotion.isChecked());

        startActivity(intent);
    }
}