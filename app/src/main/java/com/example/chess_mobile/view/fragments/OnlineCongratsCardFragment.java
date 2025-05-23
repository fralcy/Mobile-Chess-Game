package com.example.chess_mobile.view.fragments;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.player.Player;

public class OnlineCongratsCardFragment extends CongratsCardFragment {
    protected static final String RESULT = "RESULT";
    protected static final String MAIN_PLAYER = "MAIN_PLAYER";

    protected Player mainPlayer;
    public static CongratsCardFragment newInstance(Result result, Player mainPlayer) {
        CongratsCardFragment fragment = new OnlineCongratsCardFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULT, result);
        args.putSerializable(MAIN_PLAYER, mainPlayer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            result = (Result) getArguments().getSerializable(RESULT);
            mainPlayer = (Player) getArguments().getSerializable(MAIN_PLAYER);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (result.winner() == EPlayer.NONE) return;
        TextView message = view.findViewById(R.id.congratsTextMessage);
        int  colorBorder= 0, iconCongrats = 0;
        String messageTitle = result.getResultByPlayer(mainPlayer.getColor()).toUpperCase();
        if (result.winner() != mainPlayer.getColor()) {
            colorBorder = R.color.red;
            message.setTextColor(getResources().getColor(R.color.black, null));
            iconCongrats = R.drawable.asset_icon_simple_lose;
        }

        Drawable drawable = ContextCompat.getDrawable(requireContext(), iconCongrats);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            message.setCompoundDrawables(null, null, drawable, null);
        }

        message.setText(messageTitle);
        message.setCompoundDrawables(null, null, drawable, null);

        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.white, null)); // background color
        border.setStroke(8, getResources().getColor(colorBorder, null)); // width & color
        border.setCornerRadius(12); // optional corner radius
        message.setBackground(border);

        TextView des = view.findViewById(R.id.congratsTextDescription);
        des.setText(result.getPurpose());
    }
}
