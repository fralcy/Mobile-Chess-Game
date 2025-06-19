package com.example.chess_mobile.view.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.PlayerUpdateRequest;
import com.example.chess_mobile.services.http.HttpClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class EditInfoActivity extends AppCompatActivity {
    private EditText playerNameEditText;
    private EditText emailEditText;
    private Button saveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        bindView();
        loadInfo();
        setUpCancelButton();
        setUpSaveButton();

    }
    public void bindView() {
        this.playerNameEditText = findViewById(R.id.editInfoTextName);
        this.emailEditText = findViewById(R.id.editInfoTextEmail);
        this.saveButton = findViewById(R.id.btnSaveEditInfo);
        this.cancelButton= findViewById(R.id.btnCancelEditInfo);
    }
    public void loadInfo() {
        String playerName = this.getIntent().getStringExtra(InfoActivity.playerNameExtraString);
        this.playerNameEditText.setText(playerName);
        String playerEmail = this.getIntent().getStringExtra(InfoActivity.playerEmailExtraString);
        this.emailEditText.setText(playerEmail);
        this.emailEditText.setEnabled(false);
    }
    public void setUpCancelButton() {
        this.cancelButton.setOnClickListener(v->{
            finish();
        });
    }
    public void setUpSaveButton() {
        this.saveButton.setOnClickListener(v->{
            postUpdateRequest();
        });
    }
    public void showDialog(String title, String message, Class previousActivity) {
        new AlertDialog.Builder(EditInfoActivity.this).setTitle(title).
                setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Back", (dialogInterface, i) -> {
                    startActivity(new Intent( EditInfoActivity.this,previousActivity));
                    finish();
                }).show();
    }
    public void postUpdateRequest() {

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) {
            showDialog("You need to login","Login to use this app",LoginActivity.class);
            return;
        }
        user.getIdToken(true).addOnCompleteListener(task->{
            if(task.isSuccessful()) {
                String token= task.getResult().getToken();
                HttpClient httpClient= new HttpClient();
                Log.d("TOKEN",token);
                String url = HttpClient.BASE_URL+"player/update";
                Map<String,String> httpHeader = new HashMap<>();
                httpHeader.put("Authorization","Bearer "+token);
                PlayerUpdateRequest request = new PlayerUpdateRequest(EditInfoActivity.this.playerNameEditText.getText().toString());
                //Log.d("REQUEST", request.getPlayerName());
                Gson gson = new Gson();
                String json = gson.toJson(request);
                RequestBody requestBody = RequestBody.create(json,MediaType.parse("application/json; charset=utf-8"));
                Callback callback= new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(()->{
                            Toast.makeText(EditInfoActivity.this,"Some thing wen wrong, please try again",Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()) {
                            runOnUiThread(()->{
                                Toast.makeText(EditInfoActivity.this,"Update info success",Toast.LENGTH_LONG).show();
                            });

                        }
                        else {
                            runOnUiThread(()->{
                                Log.d("FAIL","RESPONSE UNSUCCESS");
                                Toast.makeText(EditInfoActivity.this,"Something went wrong, please try again",Toast.LENGTH_LONG).show();
                            });
                        }

                    }
                };
                httpClient.put(url,httpHeader,requestBody,callback);




            }
            else {

                    showDialog("Something went wrong","Back to the previous screen", InfoActivity.class);

            }
        });


    }
}