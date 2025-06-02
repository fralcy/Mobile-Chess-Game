package com.example.chess_mobile.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.utils.implementations.TimeFormater;
import com.example.chess_mobile.view_model.implementations.ChessBoardViewModel;

public class PlayerCardFragment extends Fragment {
    private static final String PLAYER = "player";
    private static final String TYPE = "matchType";
    ChessBoardViewModel _chessboardViewModel;
    PlayerChess _player;
    EMatch _matchType;
    TextView _timeText;

    public PlayerCardFragment() {
        // Required empty public constructor
    }
    public static PlayerCardFragment newInstance(PlayerChess player, EMatch matchType) {
        PlayerCardFragment fragment = new PlayerCardFragment();
        Bundle args = new Bundle();
        args.putSerializable(PLAYER, player);
        args.putSerializable(TYPE, matchType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this._player = (PlayerChess) getArguments().getSerializable(PLAYER);
            this._matchType = (EMatch) getArguments().getSerializable(TYPE);
        }

        this._chessboardViewModel =
                new ViewModelProvider(requireActivity()).get(ChessBoardViewModel.getChessViewModel(this._matchType));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_player_card, container, false);
        ((TextView)view.findViewById(R.id.playerNameText)).setText(this._player.getName());
        this._timeText = view.findViewById(R.id.timeText);

        this.setListenOnPlayerTime();

        return view;
    }

    private void setListenOnPlayerTime() {
        if (this._player.getColor() == EPlayer.WHITE) {
            this._chessboardViewModel.getWhiteTimer().observe(
                    getViewLifecycleOwner(),
                    duration -> this._timeText.setText(TimeFormater.formatDuration(duration)));
        } else if (this._player.getColor() == EPlayer.BLACK) {
            this._chessboardViewModel.getBlackTimer().observe(
                    getViewLifecycleOwner(),
                    duration -> this._timeText.setText(TimeFormater.formatDuration(duration)));
        }
    }
}