package com.example.chess_mobile.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;

import java.time.Duration;

public class AIMatchActivity extends AppCompatActivity {
    private EPlayer _selectedColor = EPlayer.WHITE;
    private int _selectedDifficulty = 1;

    private Button _buttonWhite, _buttonRandom, _buttonBlack;
    private Button _buttonLevel1, _buttonLevel2, _buttonLevel3, _buttonLevel4, _buttonLevel5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_match);

        bindViews();
        setupColorButtons();
        setupDifficultyButtons();
        setupPlayButton();

        // Set default selections
        updateColorButtons();
        updateDifficultyButtons();
    }

    private void bindViews() {
        _buttonWhite = findViewById(R.id.aiMatchButtonWhite);
        _buttonRandom = findViewById(R.id.aiMatchButtonRandom);
        _buttonBlack = findViewById(R.id.aiMatchButtonBlack);

        _buttonLevel1 = findViewById(R.id.aiMatchButtonLevel1);
        _buttonLevel2 = findViewById(R.id.aiMatchButtonLevel2);
        _buttonLevel3 = findViewById(R.id.aiMatchButtonLevel3);
        _buttonLevel4 = findViewById(R.id.aiMatchButtonLevel4);
        _buttonLevel5 = findViewById(R.id.aiMatchButtonLevel5);
    }

    private void setupColorButtons() {
        _buttonWhite.setOnClickListener(v -> {
            _selectedColor = EPlayer.WHITE;
            updateColorButtons();
        });

        _buttonRandom.setOnClickListener(v -> {
            _selectedColor = Math.random() < 0.5 ? EPlayer.WHITE : EPlayer.BLACK;
            updateColorButtons();
        });

        _buttonBlack.setOnClickListener(v -> {
            _selectedColor = EPlayer.BLACK;
            updateColorButtons();
        });
    }

    private void setupDifficultyButtons() {
        _buttonLevel1.setOnClickListener(v -> {
            _selectedDifficulty = 1;
            updateDifficultyButtons();
        });

        _buttonLevel2.setOnClickListener(v -> {
            _selectedDifficulty = 2;
            updateDifficultyButtons();
        });

        _buttonLevel3.setOnClickListener(v -> {
            _selectedDifficulty = 3;
            updateDifficultyButtons();
        });

        _buttonLevel4.setOnClickListener(v -> {
            _selectedDifficulty = 4;
            updateDifficultyButtons();
        });

        _buttonLevel5.setOnClickListener(v -> {
            _selectedDifficulty = 5;
            updateDifficultyButtons();
        });
    }

    private void setupPlayButton() {
        findViewById(R.id.aiMatchButtonPlay).setOnClickListener(v -> startAIMatch());
    }

    private void updateColorButtons() {
        // Reset all
        _buttonWhite.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        _buttonRandom.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        _buttonBlack.setBackgroundResource(R.drawable.rounded_gray_button_bg);

        // Highlight selected
        if (_selectedColor == EPlayer.WHITE) {
            _buttonWhite.setBackgroundResource(R.drawable.rounded_button_bg);
        } else if (_selectedColor == EPlayer.BLACK) {
            _buttonBlack.setBackgroundResource(R.drawable.rounded_button_bg);
        } else {
            _buttonRandom.setBackgroundResource(R.drawable.rounded_button_bg);
        }
    }

    private void updateDifficultyButtons() {
        Button[] buttons = {_buttonLevel1, _buttonLevel2, _buttonLevel3, _buttonLevel4, _buttonLevel5};

        // Reset all
        for (Button button : buttons) {
            button.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        }

        // Highlight selected
        if (_selectedDifficulty >= 1 && _selectedDifficulty <= 5) {
            buttons[_selectedDifficulty - 1].setBackgroundResource(R.drawable.rounded_button_bg);
        }
    }

    private void startAIMatch() {
        String playerName = "Player";
        String aiName = "AI Level " + _selectedDifficulty;

        PlayerChess humanPlayer = new PlayerChess("human", playerName, _selectedColor);
        PlayerChess aiPlayer = new PlayerChess("ai", aiName, _selectedColor.getOpponent());

        Intent intent = new Intent(this, RoomChessActivity.class);
        intent.putExtra(RoomChessActivity.MAIN_PLAYER, humanPlayer);
        intent.putExtra(RoomChessActivity.OPPONENT_PLAYER, aiPlayer);
        intent.putExtra(RoomChessActivity.DURATION, Duration.ofMinutes(10));
        intent.putExtra(RoomChessActivity.TYPE, EMatch.AI);
        intent.putExtra("AI_DIFFICULTY", _selectedDifficulty);

        startActivity(intent);
    }
}