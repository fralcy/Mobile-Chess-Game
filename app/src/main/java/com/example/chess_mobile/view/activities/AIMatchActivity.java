package com.example.chess_mobile.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.view_model.IAIMatchViewModel;

import java.time.Duration;

public class AIMatchActivity extends AppCompatActivity implements IAIMatchViewModel {

    private EPlayer selectedColor = EPlayer.WHITE;
    private int selectedDifficulty = 1;

    private Button[] colorButtons;
    private Button[] difficultyButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_match);

        initializeButtons();
        setupClickListeners();
    }

    private void initializeButtons() {
        // Color buttons - default: White selected
        colorButtons = new Button[]{
                findViewById(R.id.aiMatchButtonWhite),   // index 0 - WHITE
                findViewById(R.id.aiMatchButtonRandom),  // index 1 - NONE (random)
                findViewById(R.id.aiMatchButtonBlack)    // index 2 - BLACK
        };

        // Difficulty buttons - default: Level 1 selected
        difficultyButtons = new Button[]{
                findViewById(R.id.aiMatchButtonLevel1),  // index 0 - level 1
                findViewById(R.id.aiMatchButtonLevel2),  // index 1 - level 2
                findViewById(R.id.aiMatchButtonLevel3),  // index 2 - level 3
                findViewById(R.id.aiMatchButtonLevel4),  // index 3 - level 4
                findViewById(R.id.aiMatchButtonLevel5)   // index 4 - level 5
        };

        findViewById(R.id.aiMatchButtonPlay).setOnClickListener(v -> startAIGame());
    }

    private void setupClickListeners() {
        // Color selection listeners
        colorButtons[0].setOnClickListener(v -> selectColor(EPlayer.WHITE));
        colorButtons[1].setOnClickListener(v -> selectColor(EPlayer.NONE));
        colorButtons[2].setOnClickListener(v -> selectColor(EPlayer.BLACK));

        // Difficulty selection listeners
        for (int i = 0; i < difficultyButtons.length; i++) {
            final int level = i + 1;
            difficultyButtons[i].setOnClickListener(v -> selectDifficulty(level));
        }
    }

    private void selectColor(EPlayer color) {
        selectedColor = color;
        updateColorButtons();
        // Call interface method for Toast feedback
        onColorSelected(color, this);
    }

    private void selectDifficulty(int level) {
        selectedDifficulty = level;
        updateDifficultyButtons();
        // Call interface method for Toast feedback
        onDifficultySelected(level, this);
    }

    private void updateColorButtons() {
        EPlayer[] colors = {EPlayer.WHITE, EPlayer.NONE, EPlayer.BLACK};

        for (int i = 0; i < colorButtons.length; i++) {
            if (colors[i] == selectedColor) {
                // Selected - green background
                colorButtons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_bg));
            } else {
                // Unselected - gray background
                colorButtons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));
            }
        }
    }

    private void updateDifficultyButtons() {
        for (int i = 0; i < difficultyButtons.length; i++) {
            if (i + 1 == selectedDifficulty) {
                // Selected - green background
                difficultyButtons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_bg));
            } else {
                // Unselected - gray background
                difficultyButtons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_gray_button_bg));
            }
        }
    }

    private void startAIGame() {
        // Validate using default interface method
        if (!validateSettings(selectedColor, selectedDifficulty, this)) {
            return;
        }

        // Handle random color using default interface method
        EPlayer finalColor = handleRandomColor(selectedColor, this);

        // Create players
        Player humanPlayer = new Player("human", "You", finalColor);
        Player aiPlayer = new Player("ai", "AI (" + getDifficultyName(selectedDifficulty) + ")",
                finalColor.getOpponent());

        // Start AI game
        Intent intent = new Intent(this, RoomChessActivity.class);
        intent.putExtra(RoomChessActivity.MAIN_PLAYER, humanPlayer);
        intent.putExtra(RoomChessActivity.OPPONENT_PLAYER, aiPlayer);
        intent.putExtra(RoomChessActivity.DURATION, Duration.ofMinutes(10));
        intent.putExtra(RoomChessActivity.TYPE, EMatch.AI);
        intent.putExtra("AI_DIFFICULTY", selectedDifficulty);

        startActivity(intent);
    }
}