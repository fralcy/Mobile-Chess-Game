package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.chess_mobile.dto.request.MoveRequest;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.services.websocket.implementations.SocketManager;
import com.example.chess_mobile.view.fragments.ChessBoardFragment;
import com.example.chess_mobile.view.fragments.CongratsCardFragment;
import com.example.chess_mobile.view.fragments.DrawResignActionFragment;
import com.example.chess_mobile.view.interfaces.DrawResignActionListener;
import com.example.chess_mobile.view.fragments.OnlineCongratsCardFragment;
import com.example.chess_mobile.view.fragments.PlayerCardFragment;
import com.example.chess_mobile.view.interfaces.IGameOverListener;
import com.example.chess_mobile.view_model.enums.ESocketMessageType;
import com.example.chess_mobile.view_model.implementations.ChessBoardViewModel;
import com.example.chess_mobile.view_model.interfaces.IChessViewModel;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.Optional;

public class RoomChessActivity extends AppCompatActivity implements IGameOverListener {
    public static final String MAIN_PLAYER = "main";
    public static final String OPPONENT_PLAYER = "opponent";
    public static final String DURATION = "duration";
    public static final String TYPE = "type";
    public static final String MATCH_ID = "matchID";
    public static String publicMatchId;

    private EMatch matchType;
    private PlayerChess main;
    private Duration duration;
    private PlayerChess opponent;
    private String matchId;


    private EEndReason endReason;
    private Result result;
    private ChessBoardFragment chessBoardFragment;

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
                .ofNullable((PlayerChess) getIntent().getSerializableExtra(MAIN_PLAYER))
                .orElse(new PlayerChess("0", "Black Player", EPlayer.WHITE));
        this.opponent = Optional
                .ofNullable((PlayerChess) getIntent().getSerializableExtra(OPPONENT_PLAYER))
                .orElse(new PlayerChess("-1", "White Player", EPlayer.BLACK));
        this.duration = Optional
                .ofNullable((Duration) getIntent().getSerializableExtra(DURATION))
                .orElse(Duration.ofSeconds(600));
        this.matchType = Optional
                .ofNullable((EMatch) getIntent().getSerializableExtra(TYPE))
                .orElse(EMatch.RANKED);
 //               .orElse(EMatch.LOCAL);
//        PieceImagesInstance.setTheme(EPieceImageTheme.COMIC);
        this.matchId = Optional
                .ofNullable(getIntent().getStringExtra(MATCH_ID))
                .orElse("");
        publicMatchId = Optional
                .ofNullable(getIntent().getStringExtra(MATCH_ID))
                .orElse("");
        if (savedInstanceState == null) {
            bindFragments(this.matchType, this.main, this.opponent, this.duration);
        }

    }

    private ChessBoardFragment initChessBoardFragment(EMatch matchType, PlayerChess mainPlayer) {
        return switch (matchType) {
//            case LOCAL -> LocalChessBoardFragment.newInstance(8, mainPlayer);
            case RANKED, AI, PRIVATE -> ChessBoardFragment.newInstance(8, mainPlayer, matchType);
            default -> ChessBoardFragment.newInstance(8, mainPlayer, matchType);
        };
    }

    private Optional<DrawResignActionFragment> initDrawResignActionFragment
            (EMatch matchType, ChessBoardFragment chessBoardFragment) {
        DrawResignActionFragment actionFragment = switch (matchType) {
            case RANKED, AI, PRIVATE, LOCAL -> DrawResignActionFragment.newInstance();
        };

        actionFragment.setActionListener(new DrawResignActionListener() {
            @Override
            public void onDrawOffered() {
                
                chessBoardFragment.showConfirmOfferDialog();
            }

            @Override
            public void onResigned() {

                chessBoardFragment.showResignationDialog();
            }
        });
        return Optional.of(actionFragment);
    }

    private CongratsCardFragment initCongratsCardFragment(EMatch matchType, Result result,
                                                          PlayerChess mainPlayer) {
        return switch (matchType) {
            case AI, RANKED,PRIVATE -> OnlineCongratsCardFragment.newInstance(result, mainPlayer);
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
        });
    }

    private void bindFragments(EMatch matchType, PlayerChess mainPlayer, PlayerChess opponentPlayer, Duration duration) {
        ChessBoardViewModel chessBoardViewModel = new ViewModelProvider(this)
                .get(ChessBoardViewModel.getChessViewModel(matchType));

        Board board = new Board();
        chessBoardViewModel.newGame(this.matchId, EPlayer.WHITE, board.initial(), mainPlayer,
                opponentPlayer, duration);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        this.chessBoardFragment = this.initChessBoardFragment(matchType,
                chessBoardViewModel.getMainPlayer());

        this.initDrawResignActionFragment(matchType, chessBoardFragment).ifPresent(actionFragment ->
                transaction.replace(R.id.gameActionsFrameLayout, actionFragment));
        setListenOnActionFragment(chessBoardViewModel, matchType,
                chessBoardFragment, transaction);
        transaction
                .replace(R.id.playerOpponentCardFrameLayout,
                        PlayerCardFragment.newInstance(chessBoardViewModel.getOpponentPlayer(),
                                matchType))
                .replace(R.id.playerMainCardFrameLayout,
                        PlayerCardFragment.newInstance(chessBoardViewModel.getMainPlayer(), matchType))
                .replace(R.id.roomChessFrameLayoutBoard, chessBoardFragment)
                .commit();

    }

    private void backToHome() {
        /*startActivity(new Intent(this, MainActivity.class));
        this.finish();*/
        Intent intent = new Intent(this, GameModeSelectionActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onGameOver() {
        Result result= this.chessBoardFragment.getChessBoardViewModel().getResult().getValue();
        String textMessage = result.getResult();
        Log.d("RESULT-TEXXXXT", textMessage);
        String endReasonMessage = result.getPurpose();
        if(textMessage.equals("Draw")) {
            textMessage="Draw";
        }
        else if((textMessage.equals("White win")&&this.main.getColor()==EPlayer.WHITE)||(textMessage.equals("Black win")&&this.main.getColor()==EPlayer.BLACK)) {
            textMessage ="You win!";
        }
        else {
            textMessage="You lose!";
        }

        Dialog dialog = new Dialog(this, R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_result);
        ((TextView) dialog.findViewById(R.id.textResult)).setText(textMessage);
        ((TextView) dialog.findViewById(R.id.textReasonEnd)).setText(endReasonMessage.toUpperCase());
         dialog.findViewById(R.id.buttonBack_result).setOnClickListener(v->{
            backToHome();
        });
        dialog.show();

        /*((AppCompatButton) dialog.findViewById(R.id.buttonYes)).setText(R.string.new_game_label);
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
        dialog.show();*/

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        MoveRequest moveRequest = new MoveRequest(ESocketMessageType.RESIGN, this.matchId,null);
        Gson gson = new Gson();
        String json = gson.toJson(moveRequest);
        SocketManager.getInstance().sendMessage(json, "/topic"+String.format(SocketManager.CHESS_MOVE_ENDPOINT_TEMPLATE,this.matchId));
    }
}