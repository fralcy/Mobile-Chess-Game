package com.example.chess_mobile.view.activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.view.fragments.ChessBoardFragment;
import com.example.chess_mobile.view.fragments.CongratsCardFragment;
import com.example.chess_mobile.view.fragments.DrawResignActionFragment;
import com.example.chess_mobile.view.fragments.DrawResignActionListener;
import com.example.chess_mobile.view.fragments.LocalChessBoardFragment;
import com.example.chess_mobile.view.fragments.OnlineCongratsCardFragment;
import com.example.chess_mobile.view.fragments.PlayerCardFragment;
import com.example.chess_mobile.view_model.ChessBoardViewModel;
import com.example.chess_mobile.view_model.IChessViewModel;

import java.time.Duration;
import java.util.Optional;

public class RoomChessActivity extends AppCompatActivity {
    public static final String MAIN_PLAYER = "main";
    public static final String OPPONENT_PLAYER = "opponent";
    public static final String DURATION = "duration";
    public static final String TYPE = "type";
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
        chessBoardViewModel.setGameState(new GameState(EPlayer.WHITE, board.initial(),
                Duration.ofMinutes(1)));
        chessBoardViewModel.setPlayers(
                new Player("0", "Black Player", EPlayer.BLACK),
                new Player("-1", "White Player", EPlayer.WHITE)
        );

        EMatch matchType = Optional
                .ofNullable((EMatch) getIntent().getSerializableExtra("match_type"))
                .orElse(EMatch.LOCAL);
//        PieceImagesInstance.setTheme(EPieceImageTheme.COMIC);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ChessBoardFragment chessBoardFragment = this.initChessBoardFragment(matchType,
                    chessBoardViewModel.getMainPlayer());

            this.initDrawResignActionFragment(matchType, chessBoardFragment).ifPresent(actionFragment ->
                    transaction.replace(R.id.gameActionsFrameLayout, actionFragment));

            transaction
                    .replace(R.id.playerOpponentCardFrameLayout, PlayerCardFragment.newInstance(chessBoardViewModel.getOpponentPlayer()))
                    .replace(R.id.playerMainCardFrameLayout, PlayerCardFragment.newInstance(chessBoardViewModel.getMainPlayer()))
                    .replace(R.id.roomChessFrameLayoutBoard, chessBoardFragment)
                    .commit();

            setListenOnActionFragment(chessBoardViewModel, matchType,
                    chessBoardFragment);
        }

    }

    private ChessBoardFragment initChessBoardFragment(EMatch matchType, Player mainPlayer) {
        return switch (matchType) {
//            case LOCAL -> LocalChessBoardFragment.newInstance(8, mainPlayer);
            case RANKED, AI, FRIEND -> ChessBoardFragment.newInstance(8, mainPlayer);
            default -> LocalChessBoardFragment.newInstance(8, mainPlayer);
        };
    }
    private Optional<DrawResignActionFragment> initDrawResignActionFragment
            (EMatch matchType, ChessBoardFragment chessBoardFragment) {
        DrawResignActionFragment actionFragment = switch (matchType) {
            case RANKED, AI, FRIEND, LOCAL -> DrawResignActionFragment.newInstance();
            default -> null;
        };

        if (actionFragment != null) {
            actionFragment.setActionListener(new DrawResignActionListener() {
                @Override
                public void onDrawOffered() {
                    chessBoardFragment.showDrawOfferDialog();
                }
                @Override
                public void onResigned() {
                    chessBoardFragment.showResignationDialog();
                }
            });
            return Optional.of(actionFragment);
        } else {
            return Optional.empty();
        }
    }

    private CongratsCardFragment initCongratsCardFragment(EMatch matchType, Result result,
                                                          Player mainPlayer) {
        return switch (matchType) {
            case AI, RANKED -> OnlineCongratsCardFragment.newInstance(result, mainPlayer);
            default -> CongratsCardFragment.newInstance(result);
        };
    }

    private void setListenOnActionFragment(IChessViewModel chessViewModel, EMatch matchType, ChessBoardFragment chessBoardFragment) {
        chessViewModel.getResult().observe(this, result -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); // NEW transaction

            if (result != null) {
                CongratsCardFragment congratsCardFragment = initCongratsCardFragment(matchType,
                        result, chessViewModel.getMainPlayer());
                transaction.replace(R.id.gameActionsFrameLayout, congratsCardFragment);
            } else {
                this.initDrawResignActionFragment(matchType, chessBoardFragment).ifPresent(actionFragment ->
                        transaction.replace(R.id.gameActionsFrameLayout, actionFragment)
                );
            }

            transaction
                    .replace(R.id.playerOpponentCardFrameLayout, PlayerCardFragment.newInstance(chessViewModel.getOpponentPlayer()))
                    .replace(R.id.playerMainCardFrameLayout, PlayerCardFragment.newInstance(chessViewModel.getMainPlayer()))
                    .replace(R.id.roomChessFrameLayoutBoard, chessBoardFragment)
                    .commit();
        });
    }

}