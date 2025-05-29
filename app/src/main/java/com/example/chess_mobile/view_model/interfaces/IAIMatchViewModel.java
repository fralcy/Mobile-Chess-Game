package com.example.chess_mobile.view_model.interfaces;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.view.activities.RoomChessActivity;

public interface IAIMatchViewModel {
    default void onColorSelected(EPlayer color, Context context) {
        String colorName = color == EPlayer.WHITE ? "White" :
                color == EPlayer.BLACK ? "Black" : "Random";
        Toast.makeText(context, "You will play as: " + colorName, Toast.LENGTH_SHORT).show();
    }

    default void onDifficultySelected(int level, Context context) {
        String[] difficulties = {"Beginner", "Easy", "Normal", "Hard", "Expert"};
        String difficultyName = level > 0 && level <= 5 ? difficulties[level-1] : "Unknown";
        Toast.makeText(context, "AI Difficulty: " + difficultyName, Toast.LENGTH_SHORT).show();
    }

    default void onPlayButtonClicked(Context context) {
        Toast.makeText(context, "Starting AI match...", Toast.LENGTH_SHORT).show();
        initializeAIGame(context);
    }

    default void initializeAIGame(Context context) {
        // TODO: Initialize AI game with selected settings
        Intent intent = new Intent(context, RoomChessActivity.class);
        intent.putExtra("GAME_MODE", "AI");
        context.startActivity(intent);
    }

    // New methods for better UI management
    default void updateColorButtons(EPlayer selectedColor, View buttonWhite, View buttonRandom, View buttonBlack) {
        // Reset all color buttons
        buttonWhite.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        buttonRandom.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        buttonBlack.setBackgroundResource(R.drawable.rounded_gray_button_bg);

        // Highlight selected color
        switch (selectedColor) {
            case WHITE -> buttonWhite.setBackgroundResource(R.drawable.rounded_button_bg);
            case BLACK -> buttonBlack.setBackgroundResource(R.drawable.rounded_button_bg);
            case NONE -> buttonRandom.setBackgroundResource(R.drawable.rounded_button_bg);
        }
    }

    default void updateDifficultyButtons(int selectedDifficulty,
                                         View button1, View button2, View button3,
                                         View button4, View button5) {
        // Reset all difficulty buttons
        View[] buttons = {button1, button2, button3, button4, button5};
        for (View button : buttons) {
            button.setBackgroundResource(R.drawable.rounded_gray_button_bg);
        }

        // Highlight selected difficulty
        if (selectedDifficulty >= 1 && selectedDifficulty <= 5) {
            buttons[selectedDifficulty - 1].setBackgroundResource(R.drawable.rounded_button_bg);
        }
    }

    default EPlayer handleRandomColor(EPlayer selectedColor, Context context) {
        if (selectedColor == EPlayer.NONE) {
            EPlayer randomColor = Math.random() < 0.5 ? EPlayer.WHITE : EPlayer.BLACK;
            Toast.makeText(context, "You got: " + (randomColor == EPlayer.WHITE ? "White" : "Black"),
                    Toast.LENGTH_SHORT).show();
            return randomColor;
        }
        return selectedColor;
    }

    default void setupColorButtonClickListener(View button, EPlayer color,
                                               ColorSelectionCallback callback) {
        button.setOnClickListener(v -> callback.onColorSelected(color));
    }

    default void setupDifficultyButtonClickListener(View button, int level,
                                                    DifficultySelectionCallback callback) {
        button.setOnClickListener(v -> callback.onDifficultySelected(level));
    }

    default String getDifficultyName(int level) {
        String[] difficulties = {"Beginner", "Easy", "Normal", "Hard", "Expert"};
        return level > 0 && level <= 5 ? difficulties[level-1] : "Unknown";
    }

    default boolean validateSettings(EPlayer selectedColor, int selectedDifficulty, Context context) {
        if (selectedDifficulty < 1 || selectedDifficulty > 5) {
            Toast.makeText(context, "Please select a difficulty level", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Callback interfaces for clean separation
    interface ColorSelectionCallback {
        void onColorSelected(EPlayer color);
    }

    interface DifficultySelectionCallback {
        void onDifficultySelected(int level);
    }
}