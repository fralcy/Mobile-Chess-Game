package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.google.firebase.auth.FirebaseAuth;

public class InfoActivity extends AppCompatActivity {
    private TextView playerName;
    private TextView playerEmail;
    private TextView rank;
    private TextView elo;
    private TextView matches;
    private Button editProfileButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_info);
        bindView();
        setUpLogoutButton();
    }
    public void bindView() {

        this.playerName= findViewById(R.id.profile_name);
        this.playerEmail = findViewById(R.id.profile_email);
        this.rank= findViewById(R.id.profile_rank);
        this.elo= findViewById(R.id.profile_elo);
        this.matches = findViewById(R.id.profile_matches);
        this.editProfileButton = findViewById(R.id.btnEditInfo);
        this.logoutButton = findViewById(R.id.btnLogout);
    }
    public void setUpLogoutButton() {
        this.logoutButton.setOnClickListener(v->{
            Dialog dialog = new Dialog(this, R.style.Dialog_Full_Width);
            dialog.setContentView(R.layout.layout_confirmation_dialog);
            ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(R.string.logout);
            dialog.findViewById(R.id.buttonYes).setOnClickListener(view->{
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();

            });
            dialog.findViewById(R.id.buttonNo).setOnClickListener(view->{
                dialog.dismiss();
            });
            dialog.show();

        });
    }
}