package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.os.Bundle;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.IGameModeSelectionViewModel;

public class GameModeSelectionActivity extends Activity implements IGameModeSelectionViewModel {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode_selection);

    }
}
