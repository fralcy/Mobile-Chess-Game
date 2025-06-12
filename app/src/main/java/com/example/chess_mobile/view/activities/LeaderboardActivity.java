package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.services.http.HttpClient;
import com.example.chess_mobile.adapter.LeaderboardAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LeaderboardActivity extends Activity {
    private List<Player> listPlayer;
    private RecyclerView recyclerView;
    private void getTop10() {
        List<Player> res = new ArrayList<>();
        Collections.sort(listPlayer);
        res= listPlayer.subList(0,10);
        this.listPlayer=res;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        bindView();
        loadListPlayer();
    }
    public void bindView() {
        recyclerView=findViewById(R.id.recyclerLeaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void loadListPlayer() {
        HttpClient httpClient = new HttpClient();
        String url = HttpClient.BASE_URL+"allPlayer";
        Callback callBack = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String json = response.body().string();
                    runOnUiThread(()->{

                        Type listType = new TypeToken<List<Player>>() {}.getType();
                            listPlayer = new Gson().fromJson(json,listType);
                            getTop10();
                            recyclerView.setAdapter(new LeaderboardAdapter(listPlayer));




                    });
                }
            }
        };
        httpClient.get(url,null,callBack);
    }
}