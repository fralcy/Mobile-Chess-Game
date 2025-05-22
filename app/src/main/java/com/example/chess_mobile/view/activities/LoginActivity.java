package com.example.chess_mobile.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.chess_mobile.view_model.ILoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements ILoginViewModel {
    IAuthenticationService authenticationService;
    Button buttonLogin;
    EditText txtEmail;
    EditText txtPassword;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this   , MainActivity.class));
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
        bindView();
        bindEventHandler();
    }

    private void login(String email, String password) {
        FirebaseAccount acc = new FirebaseAccount(email, password);
        authenticationService.login(acc, isSuccess-> {
            if (isSuccess) {
                Toast.makeText(this, "Login Success! ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Login Failed! ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindEventHandler() {
        buttonLogin.setOnClickListener(l -> {
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            login(email, password);
        });
    }

    private void bindView() {
        buttonLogin = findViewById(R.id.loginButton);
        txtEmail =  findViewById(R.id.loginTextEmail);
        txtPassword = findViewById(R.id.loginTextPassword);
    }

}