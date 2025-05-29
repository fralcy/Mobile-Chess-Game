package com.example.chess_mobile.view_model.interfaces;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.chess_mobile.model.authentication.firebase.FirebaseAccount;
import com.example.chess_mobile.model.authentication.firebase.FirebaseAuthenticationService;
import com.example.chess_mobile.model.authentication.interfaces.IAuthenticationService;
import com.example.chess_mobile.view.activities.MainActivity;
import com.example.chess_mobile.view.activities.RegisterActivity;

public interface ILoginViewModel {
    default void onLoginButtonClicked(String email, String password, Context context) {
        if (!validateLoginInput(email, password, context)) {
            return;
        }

        showLoadingMessage(context);
        performLogin(email, password, context);
    }

    default void onRegisterLinkClicked(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    default void onForgotPasswordClicked(String email, Context context) {
        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(context, "Please enter email first", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Implement forgot password logic
        Toast.makeText(context, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
    }

    default boolean validateLoginInput(String email, String password, Context context) {
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

        return true;
    }

    default void performLogin(String email, String password, Context context) {
        IAuthenticationService authService = new FirebaseAuthenticationService();
        FirebaseAccount account = new FirebaseAccount(email, password);

        authService.login(account, isSuccess -> {
            if (isSuccess) {
                onLoginSuccess(context);
            } else {
                onLoginFailure(context);
            }
        });
    }

    default void onLoginSuccess(Context context) {
        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    default void onLoginFailure(Context context) {
        Toast.makeText(context, "Login failed. Please check your credentials.", Toast.LENGTH_LONG).show();
    }

    default void showLoadingMessage(Context context) {
        Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show();
    }

    default boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}