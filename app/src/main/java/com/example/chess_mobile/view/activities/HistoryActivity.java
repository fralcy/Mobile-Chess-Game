package com.example.chess_mobile.view.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.adapter.HistoryAdapter;
import com.example.chess_mobile.dto.response.MatchHistory;
import com.example.chess_mobile.dto.response.MatchResponse;
import com.example.chess_mobile.dto.response.MatchResponseWithPlayerName;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.services.http.HttpClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {
    private List<MatchHistory> listMatchResponse;
    private RecyclerView recycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        bindView();
        fetchListMatch();
    }
    public void bindView() {
        this.recycleView= findViewById(R.id.historyRecyclerView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void showDialog(String title, String message, Class previousActivity) {
        new AlertDialog.Builder(HistoryActivity.this).setTitle(title).
                setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Back", (dialogInterface, i) -> {
                    startActivity(new Intent( HistoryActivity.this,previousActivity));
                    finish();
                }).show();
    }
    public void fetchListMatch() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) {
            showDialog("You need to login","Login to use this app",LoginActivity.class);
            return;
        }
        user.getIdToken(true).addOnCompleteListener(task->{
            if(task.isSuccessful()) {
                String token = task.getResult().getToken();
                HttpClient httpClient = new HttpClient(5000);
                Log.d("TOKEN", token);
                String url = HttpClient.BASE_URL + "getMatches";
                Map<String, String> httpHeader = new HashMap<>();
                httpHeader.put("Authorization", "Bearer " + token);

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()) {
                            Gson gson = new Gson();
                            String json= response.body().string();
                            Log.d("JSON",json);
                            Type listType = new TypeToken<List<MatchHistory>>() {}.getType();
                            listMatchResponse= gson.fromJson(json,listType);


                            runOnUiThread(()->{
                                List<MatchHistory> listmatch=filterMatches();
                                recycleView.setAdapter(new HistoryAdapter(listmatch));
                            });
                            for(int i=0;i<listMatchResponse.size();i++) {
                                Log.d("MATCH",listMatchResponse.get(i).getMatchId());
                            }


                        }
                        else {
                            Log.d("FAIL","FAIL");
                        }

                    }

                };
                httpClient.get(url,httpHeader,callback);
            }
        });
    }
    public List<MatchHistory> filterMatches() {
        List<MatchHistory> res= new ArrayList<>();
        for(MatchHistory match:listMatchResponse) {
            if(match.getMatchState().equals("WHITE_WIN")||match.getMatchState().equals("WHITE_WIN")||match.getMatchState().equals("DRAW")) {
                res.add(match);
            }
            //listMatchResponse.remove(match);
        }
        return res;
    }



}