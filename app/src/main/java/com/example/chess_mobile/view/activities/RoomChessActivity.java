package com.example.chess_mobile.view.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.game_states.Board;
import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.game_states.GameState;
import com.example.chess_mobile.view.fragments.ChessBoardFragment;
import com.example.chess_mobile.view_model.ChessBoardViewModel;

public class RoomChessActivity extends AppCompatActivity {

    FrameLayout frameLayoutBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_chess);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        frameLayoutBoard = findViewById(R.id.roomChessFrameLayoutBoard);

        ChessBoardViewModel chessBoardViewModel = new ViewModelProvider(this)
                .get(ChessBoardViewModel.class);

        Board board = new Board();
        chessBoardViewModel.setGameState(new GameState(EPlayer.WHITE, board.initial()));

        // Thêm ChessBoardFragment vào FrameLayout nếu chưa có
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.roomChessFrameLayoutBoard, ChessBoardFragment.newInstance(8))
                    .commit();
        }
    }
}