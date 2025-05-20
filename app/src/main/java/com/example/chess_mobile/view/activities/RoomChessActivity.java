package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

public class RoomChessActivity extends AppCompatActivity implements IGameOverListener {
    public static final String MAIN_PLAYER = "main";
    public static final String OPPONENT_PLAYER = "opponent";
    public static final String DURATION = "duration";
    public static final String TYPE = "type";

    private EMatch matchType;
    private Player main;
    private Duration duration;
    private Player opponent;

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
        this.main = Optional
                .ofNullable((Player) getIntent().getSerializableExtra(MAIN_PLAYER))
                .orElse(new Player("0", "Black Player", EPlayer.BLACK));
        this.opponent = Optional
                .ofNullable((Player) getIntent().getSerializableExtra(OPPONENT_PLAYER))
                .orElse(new Player("-1", "White Player", EPlayer.WHITE));
        this.duration = Optional
                .ofNullable((Duration) getIntent().getSerializableExtra(DURATION))
                .orElse(Duration.ofSeconds(600));
        this.matchType = Optional
                .ofNullable((EMatch) getIntent().getSerializableExtra(TYPE))
                .orElse(EMatch.LOCAL);
//        PieceImagesInstance.setTheme(EPieceImageTheme.COMIC);

        if (savedInstanceState == null) {
            bindFragments(this.matchType, this.main, this.opponent, this.duration);
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

    private void setListenOnActionFragment(IChessViewModel chessViewModel, EMatch matchType,
                                           ChessBoardFragment chessBoardFragment,FragmentTransaction transaction) {
        chessViewModel.getResult().observe(this, result -> {
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
                    .replace(R.id.roomChessFrameLayoutBoard, chessBoardFragment);
        });
    }

    private void bindFragments(EMatch matchType, Player mainPlayer, Player opponentPlayer) {
        bindFragments(matchType, mainPlayer, opponentPlayer, Duration.ofSeconds(600)); // Default value
    }

    private void bindFragments(EMatch matchType, Player mainPlayer, Player opponentPlayer, Duration duration) {
        ChessBoardViewModel chessBoardViewModel = new ViewModelProvider(this)
                .get(ChessBoardViewModel.class);

        Board board = new Board();
        chessBoardViewModel.newGame(EPlayer.WHITE, board.initial(), mainPlayer,
                opponentPlayer, duration);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ChessBoardFragment chessBoardFragment = this.initChessBoardFragment(matchType,
                chessBoardViewModel.getMainPlayer());


        this.initDrawResignActionFragment(matchType, chessBoardFragment).ifPresent(actionFragment ->
                transaction.replace(R.id.gameActionsFrameLayout, actionFragment));

        setListenOnActionFragment(chessBoardViewModel, matchType,
                chessBoardFragment, transaction);

        transaction
                .replace(R.id.playerOpponentCardFrameLayout, PlayerCardFragment.newInstance(chessBoardViewModel.getOpponentPlayer()))
                .replace(R.id.playerMainCardFrameLayout, PlayerCardFragment.newInstance(chessBoardViewModel.getMainPlayer()))
                .replace(R.id.roomChessFrameLayoutBoard, chessBoardFragment)
                .commit();
    }

    private void backToHome() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    public void onGameOver() {
        String textMessage = "Wanna continue to play?";
        Dialog dialog = new Dialog(this, R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);
        ((TextView) dialog.findViewById(R.id.dialogMessage)).setText(textMessage);
        ((AppCompatButton) dialog.findViewById(R.id.buttonYes)).setText(R.string.new_game_label);
        ((AppCompatButton) dialog.findViewById(R.id.buttonNo)).setText(R.string.back_label);

        dialog.findViewById(R.id.buttonYes)
                .setOnClickListener(l -> {
                    bindFragments(this.matchType, this.main, this.opponent, this.duration);
                    dialog.dismiss();
                });
        dialog.findViewById(R.id.buttonNo)
                .setOnClickListener(l -> {
                    backToHome();
                    dialog.dismiss();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}