package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.interfaces.IGameModeSelectionViewModel;
import com.google.firebase.auth.FirebaseAuth;

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
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                showLoginRequestDialog();
                return;
            }
            Intent intent = new Intent(GameModeSelectionActivity.this, RankedMatchActivity.class);
            startActivity(intent);
        });

        ConstraintLayout friendMatchLayout = findViewById(R.id.friend_match);
        friendMatchLayout.setOnClickListener(v->{
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                showLoginRequestDialog();
                return;
            }
            Intent  intent = new Intent(GameModeSelectionActivity.this, FriendMatchActivity.class);
            startActivity(intent);
        });

        // Thêm AI Match
        findViewById(R.id.gameModeSelectionCardAI).setOnClickListener(v->{
            Intent intent = new Intent(GameModeSelectionActivity.this, AIMatchActivity.class);
            startActivity(intent);
        });

        // Thêm Local Match - không cần login
        findViewById(R.id.gameModeSelectionCardLocal).setOnClickListener(v->{
            Intent intent = new Intent(GameModeSelectionActivity.this, LocalMatchActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.gameModeSelectionButtonProfile).setOnClickListener(v->{
            switchToInfoActivity();
        });
    }

    private void showLoginRequestDialog() {
        String textMessage = "You have to login first!";
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_confirmation_dialog);
        ((TextView) dialog.findViewById(R.id.dialogMessage)).setText(textMessage);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l-> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l-> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void switchToInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}