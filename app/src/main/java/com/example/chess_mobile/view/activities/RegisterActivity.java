package com.example.chess_mobile.view.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.example.chess_mobile.view_model.interfaces.IRegisterViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements IRegisterViewModel {
    IAuthenticationService authenticationService;
    Button buttonRegister;
    EditText txtEmail;
    EditText txtPassword;
    EditText txtName;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("CURRENT_PLAYER", currentUser.toString());
            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
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
        bindView();
        bindEventHandler();
    }

    private void register(String email, String password, String name) {
        FirebaseAccount acc = new FirebaseAccount(email, password);
        authenticationService.register(acc, isSuccess -> {
            if(isSuccess) {
                Toast.makeText(this, "Register success", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Register failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindEventHandler() {
        buttonRegister.setOnClickListener(l -> {
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            String name = txtName.getText().toString();

            register(email, password, name);
        });
    }

    private void bindView() {
        buttonRegister = findViewById(R.id.registerButton);
        txtEmail = findViewById(R.id.registerTextEmail);
        txtPassword = findViewById(R.id.registerTextPassword);
        txtName = findViewById(R.id.registerTextName);

    }
}