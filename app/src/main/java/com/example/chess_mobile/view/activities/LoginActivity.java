package com.example.chess_mobile.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.authentication.firebase.FirebaseAccount;
import com.example.chess_mobile.model.authentication.firebase.FirebaseAuthenticationService;
import com.example.chess_mobile.model.authentication.interfaces.IAuthenticationService;
import com.example.chess_mobile.view_model.interfaces.ILoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements ILoginViewModel {
    IAuthenticationService authenticationService;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this , GameModeSelectionActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authenticationService = new FirebaseAuthenticationService();
        bindEventHandler();
    }

    @Override
    public void login(String email, String password, Context context) {
        FirebaseAccount acc = new FirebaseAccount(email, password);
        authenticationService.login(acc, (isSuccess, msg)-> {
            if (isSuccess) {
                Toast.makeText(this, "Login Success! ", Toast.LENGTH_LONG).show();
                Intent gameModeSelection = new Intent(this, GameModeSelectionActivity.class);
                startActivity(gameModeSelection);
            } else {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindEventHandler() {
        findViewById(R.id.loginButton).setOnClickListener(l -> {
            String email = ((TextView) findViewById(R.id.loginTextEmail)).getText().toString();
            String password = ((TextView) findViewById(R.id.loginTextPassword)).getText().toString();
            onLoginButtonClicked(email, password, this);
        });

        findViewById(R.id.registerLink).setOnClickListener(l -> onRegisterLinkClicked(this));
    }
}