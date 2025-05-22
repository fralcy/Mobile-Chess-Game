package com.example.chess_mobile.utils.interfaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

public interface IUIHelper {
    default void showLoadingDialog(Context context, String message) {
        // TODO: Implement proper loading dialog
        Toast.makeText(context, "Loading: " + message, Toast.LENGTH_SHORT).show();
    }

    default void hideLoadingDialog() {
        // TODO: Hide loading dialog
    }

    default void showErrorMessage(Context context, String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_LONG).show();
    }

    default void showSuccessMessage(Context context, String message) {
        Toast.makeText(context, "Success: " + message, Toast.LENGTH_SHORT).show();
    }

    default void enableButton(View button) {
        if (button != null) {
            button.setEnabled(true);
            button.setAlpha(1.0f);
        }
    }

    default void disableButton(View button) {
        if (button != null) {
            button.setEnabled(false);
            button.setAlpha(0.5f);
        }
    }
}