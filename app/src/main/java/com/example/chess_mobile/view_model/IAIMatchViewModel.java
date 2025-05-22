package com.example.chess_mobile.view_model;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
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
}