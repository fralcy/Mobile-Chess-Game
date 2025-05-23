package com.example.chess_mobile.view.activities;

import android.os.Bundle;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.IRankedMatchViewModel;
import com.example.chess_mobile.model.adapter.TopPlayersAdapter;

import java.util.ArrayList;
import java.util.List;

public class RankedMatchActivity extends AppCompatActivity implements IRankedMatchViewModel {

    private RecyclerView topPlayersRecyclerView;
    private TextView currentPlayerRank, currentPlayerElo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranked_match); // layout XML bạn đã gửi

        Context context = this;

        topPlayersRecyclerView = findViewById(R.id.topPlayersRecyclerView);
        currentPlayerRank = findViewById(R.id.currentPlayerRank);
        currentPlayerElo = findViewById(R.id.currentPlayerElo);
        Button playButton = findViewById(R.id.rankedMatchButtonPlay);

        setupRecyclerView();
        setupPlayerInfo();

        playButton.setOnClickListener(v -> onPlayButtonClicked(context));
    }

    private void setupRecyclerView() {
        topPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> topPlayers = getMockTopPlayers(); // bạn có thể dùng dữ liệu thật sau
        TopPlayersAdapter adapter = new TopPlayersAdapter(topPlayers);
        topPlayersRecyclerView.setAdapter(adapter);
    }

    private void setupPlayerInfo() {
        String rankText = "Your Rank: 7";
        String eloText = "ELO: 1450";

        currentPlayerRank.setText(rankText);
        currentPlayerElo.setText(eloText);
    }

    private List<String> getMockTopPlayers() {
        List<String> players = new ArrayList<>();
        players.add("1. Alice - 1800");
        players.add("2. Bob - 1760");
        players.add("3. Charlie - 1700");
        players.add("4. Diana - 1650");
        players.add("5. Ethan - 1600");
        players.add("6. Fiona - 1580");
        players.add("7. You - 1450");
        players.add("8. Henry - 1400");
        players.add("9. Iris - 1350");
        players.add("10. Jack - 1300");
        return players;
    }
}
