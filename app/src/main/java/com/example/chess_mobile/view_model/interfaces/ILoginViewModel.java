package com.example.chess_mobile.view_model.interfaces;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.chess_mobile.view.activities.RegisterActivity;

public interface ILoginViewModel {
    default void onLoginButtonClicked(String email, String password, Context context) {
        if (!validateLoginInput(email, password, context)) {
            return;
        }

        showLoadingMessage(context);
        login(email, password, context);
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

    void login(String email, String password, Context context);

    default void showLoadingMessage(Context context) {
        Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show();
    }

    default boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}