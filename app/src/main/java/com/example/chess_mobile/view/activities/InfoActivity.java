package com.example.chess_mobile.view.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.services.http.HttpClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {
    public static String playerNameExtraString = "player_name";
    public static String playerEmailExtraString="player_email";
    public static String playerIdExtraString = "player_id";
    private TextView playerName;
    private TextView playerEmail;
    private TextView rank;
    private TextView elo;
    private TextView matches;
    private Button editProfileButton;
    private Button logoutButton;
    private Player currentPlayer;
    private TextView playerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_info);
        bindView();
        setUpLogoutButton();

        callUserAPI();
        //loadCurrentPlayer();
    }
    public void bindView() {

        this.playerName= findViewById(R.id.profile_name);
        this.playerEmail = findViewById(R.id.profile_email);
        this.rank= findViewById(R.id.profile_rank);
        this.elo= findViewById(R.id.profile_elo);
        this.matches = findViewById(R.id.profile_matches);
        this.playerId= findViewById(R.id.profile_id);
        this.editProfileButton = findViewById(R.id.btnEditInfo);
        this.logoutButton = findViewById(R.id.btnLogout);

        ((Button) findViewById(R.id.btnColorInfo)).setOnClickListener(v -> startActivity(new Intent(InfoActivity.this, ColorSelectActivity.class)));

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
    public void callUserAPI() {
        HttpClient httpClient = new HttpClient();
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            new AlertDialog.Builder(InfoActivity.this).setTitle("Please login first").
                    setMessage("Login to use this app")
                    .setCancelable(false)
                    .setPositiveButton("Login", (dialogInterface, i) -> {
                        startActivity(new Intent( InfoActivity.this,LoginActivity.class));
                        finish();
                    }).show();
        }
        String url = HttpClient.BASE_URL+"player/"+FirebaseAuth.getInstance().getCurrentUser().getUid();


        Callback callBack = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(()->{
                    new AlertDialog.Builder(InfoActivity.this).setTitle("Some thing went wrong").
                            setMessage("Back to the previous screen")
                            .setCancelable(false)
                            .setPositiveButton("Back", (dialogInterface, i) -> {
                                startActivity(new Intent( InfoActivity.this,GameModeSelectionActivity.class));
                                finish();
                            }).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    assert response.body() != null;

                    String responseJson = response.body().string();
                    Gson gson = new Gson();
                    InfoActivity.this.currentPlayer = gson.fromJson(responseJson,Player.class);
                    runOnUiThread(()->{
                        loadCurrentPlayer();
                        setUpPressEditButton();
                    });
                }
                else {
                    runOnUiThread(()->{
                        new AlertDialog.Builder(InfoActivity.this).setTitle("Some thing went wrong").
                                setMessage("Back to the previous screen")
                                .setCancelable(false)
                                .setPositiveButton("Back", (dialogInterface, i) -> {
                                    startActivity(new Intent( InfoActivity.this,GameModeSelectionActivity.class));
                                    finish();
                                }).show();
                    });


                }
            }
        };
        httpClient.get(url,null,callBack);
    }
    public void loadCurrentPlayer() {
        InfoActivity.this.playerName.setText(this.currentPlayer.getPlayerName());
        InfoActivity.this.playerEmail.setText(this.currentPlayer.getEmail());
        InfoActivity.this.rank.setText(Integer.toString(currentPlayer.getRank()));
        InfoActivity.this.elo.setText(Integer.toString(this.currentPlayer.getScore()));
        InfoActivity.this.matches.setText(Integer.toString(this.currentPlayer.getMatches()));
        InfoActivity.this.playerId.setText("Id: "+this.currentPlayer.getPlayerId());
    }
    public void setUpPressEditButton() {
        this.editProfileButton.setOnClickListener(v->{
            Intent intent = new Intent(this, EditInfoActivity.class);
            intent.putExtra(playerNameExtraString,this.currentPlayer.getPlayerName());
            intent.putExtra(playerEmailExtraString, this.currentPlayer.getEmail());
            intent.putExtra(playerIdExtraString, this.currentPlayer.getPlayerId());
            startActivity(intent);
        });
    }
}