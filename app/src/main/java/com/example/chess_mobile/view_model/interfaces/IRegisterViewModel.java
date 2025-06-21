package com.example.chess_mobile.view_model.interfaces;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.chess_mobile.view.activities.LoginActivity;

public interface IRegisterViewModel {
    default void onRegisterButtonClicked(String name, String email, String password, Context context) {
        if (!validateRegisterInput(name, email, password, context)) {
            return;
        }

        showLoadingMessage(context);
        register(email, password, name, context);
    }

    default void onLoginLinkClicked(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    default boolean validateRegisterInput(String name, String email, String password, Context context) {
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidName(name)) {
            Toast.makeText(context, "Name must be at least 2 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void register(String email, String password, String name, Context context);

    default void showLoadingMessage(Context context) {
        Toast.makeText(context, "Creating account...", Toast.LENGTH_SHORT).show();
    }

    default boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    default boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    default boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }
}