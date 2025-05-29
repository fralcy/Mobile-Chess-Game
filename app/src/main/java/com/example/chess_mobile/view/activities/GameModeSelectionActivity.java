package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.interfaces.IGameModeSelectionViewModel;

public class GameModeSelectionActivity extends Activity implements IGameModeSelectionViewModel {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode_selection);
        bindView();

    }
    public void bindView() {
        ConstraintLayout rankedMatchLayout = findViewById(R.id.rank_match);
        rankedMatchLayout.setOnClickListener(v->{
            Intent intent = new Intent(GameModeSelectionActivity.this, RankedMatchActivity.class);
            startActivity(intent);
        });

        ConstraintLayout friendMatchLayout = findViewById(R.id.friend_match);
        friendMatchLayout.setOnClickListener(v->{
            Intent  intent = new Intent(GameModeSelectionActivity.this, FriendMatchActivity.class);
            startActivity(intent);
        });
    }
}
