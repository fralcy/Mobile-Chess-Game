package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.adapter.HistoryAdapter;
import com.example.chess_mobile.dto.response.MatchHistory;
import com.example.chess_mobile.services.http.HttpClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
    public void showDialog(String title, String errMsg, Class<?extends Activity> previousActivity) {
        new AlertDialog.Builder(HistoryActivity.this).setTitle(title).
                setMessage(errMsg)
                .setCancelable(false)
                .setPositiveButton("Back", (dialogInterface, i) -> {
                    startActivity(new Intent( HistoryActivity.this,previousActivity));
                    finish();
                }).show();
    }
    public void fetchListMatch() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) {
            showDialog("You need to login","Login to use this app", LoginActivity.class);
            return;
        }
        user.getIdToken(true).addOnCompleteListener(task -> {

            Log.d("TOKEN_SUCCESS", "Token retrieved successfully");
            if(task.isSuccessful()) {
                String token = task.getResult().getToken();

                if (token == null || token.isEmpty()) {
                    Log.d("HISTORY_ACTIVITY_TOKEN","Không thể lấy token xác thực");
                    return;
                }

                HttpClient httpClient = new HttpClient(5000);
                Log.d("HISTORY_ACTIVITY_TOKEN", token);
                String url = HttpClient.BASE_URL + "getMatches";
                Map<String, String> httpHeader = new HashMap<>();
                httpHeader.put("Authorization", "Bearer " + token);

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("API_MATCHES_LIST_FAILURE", "Error: " + e.getMessage(), e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException, JsonSyntaxException {
                        if(response.isSuccessful() && response.body() != null) {
                            try {
                                Gson gson = new Gson();
                                String json= response.body().string();
                                Log.d("API_RESPONSE", "JSON received: " + json);

                                Type listType = new TypeToken<List<MatchHistory>>() {}.getType();
                                listMatchResponse= gson.fromJson(json,listType);

                                if (listMatchResponse != null && !listMatchResponse.isEmpty()) {
                                    runOnUiThread(()->{
                                        List<MatchHistory> listMatch=filterMatches();
                                        recycleView.setAdapter(new HistoryAdapter(listMatch));
                                    });
                                    for(int i = 0; i < listMatchResponse.size(); i++) {
                                        MatchHistory match = listMatchResponse.get(i);
                                        Log.d("MATCH_INFO", String.format("Match %d - ID: %s", i + 1,
                                                match.getMatchId() != null ? match.getMatchId() : "Unknown"));
                                    }
                                } else {
                                    runOnUiThread(() -> {
                                        String errMsg = "No matches found.";
                                        Toast.makeText(HistoryActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                    });
                                }

                            } catch (JsonSyntaxException e) {
                                Log.e("JSON_PARSE_ERROR", "Failed to parse JSON", e);
                                throw e;
                            }
                        }
                        else {
                            Log.d("FAIL","FAIL");
                        }

                    }

                };
                httpClient.get(url, httpHeader, callback);
            }
        });
    }
    public List<MatchHistory> filterMatches() {
        List<MatchHistory> res= new ArrayList<>();
        for (MatchHistory match:listMatchResponse) {
            if (match.getMatchState().equals("WHITE_WIN") ||
               match.getMatchState().equals("BLACK_WIN") ||
               match.getMatchState().equals("DRAW")) {
                res.add(match);
            }
            //listMatchResponse.remove(match);
        }
        return res;
    }



}