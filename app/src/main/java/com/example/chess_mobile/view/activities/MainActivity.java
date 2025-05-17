package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindView();
        showDialog();
        }

    private void showDialog() {
        String textMessage = "Hello World";
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_confirmation_dialog);
        ((TextView) dialog.findViewById(R.id.dialogMessage)).setText(textMessage);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l-> startChess());
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l-> dialog.dismiss());
        dialog.show();
    }

    private void bindView() {
        textView = findViewById(R.id.textView);
        textView.setOnClickListener(l -> {
            startChess();
        });
    }

    private void startChess() {
        Toast.makeText(this,"Hi", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, RoomChessActivity.class));
    }
}