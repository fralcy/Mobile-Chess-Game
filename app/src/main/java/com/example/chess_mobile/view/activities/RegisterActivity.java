package com.example.chess_mobile.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.dto.request.PlayerRegisterRequest;
import com.example.chess_mobile.model.authentication.firebase.FirebaseAccount;
import com.example.chess_mobile.model.authentication.firebase.FirebaseAuthenticationService;
import com.example.chess_mobile.model.authentication.interfaces.IAuthenticationService;
import com.example.chess_mobile.view_model.interfaces.IRegisterViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements IRegisterViewModel {
    IAuthenticationService authenticationService;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("CURRENT_PLAYER", currentUser.toString());
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authenticationService = new FirebaseAuthenticationService();
        bindEventHandler();
    }

    @Override
    public void register(String email, String password, String name, Context context) {
        PlayerRegisterRequest  req = new PlayerRegisterRequest(email, password, name);

        authenticationService.register(req, (isSuccess, msg) -> {
            if(isSuccess) {
                Toast.makeText(this, "Register success", Toast.LENGTH_LONG).show();
                FirebaseAccount acc = new FirebaseAccount(email, password);
                authenticationService.login(acc, (isLoginSuccess, loginMsg)-> {
                    if (isLoginSuccess) {
                        startActivity(new Intent(this, GameModeSelectionActivity.class));
                    } else {
                        Log.e("REGISTER_LOGIN_ACTIVITY", msg);
                        startActivity(new Intent(this, LoginActivity.class));
                    }
                });
            }
            else {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindEventHandler() {
        findViewById(R.id.registerButton).setOnClickListener(l -> {
            String email = ((TextView) findViewById(R.id.registerTextEmail)).getText().toString();
            String password = ((TextView) findViewById(R.id.registerTextPassword)).getText().toString();
            String name = ((TextView) findViewById(R.id.registerTextName)).getText().toString();
            onRegisterButtonClicked(name, email, password, this);
//            if (name.isEmpty()) {
//                Toast.makeText(this,  "Please enter field name. Try again!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this,  "Email or password is incorrect. Try again!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            register(email, password, name);
        });

        findViewById(R.id.loginLink).setOnClickListener(l -> onLoginLinkClicked(this));
    }
}