package com.example.chess_mobile.view.fragments;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CongratsCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CongratsCardFragment extends Fragment {
    protected static final String RESULT = "RESULT";

    protected Result result;

    public CongratsCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param result Result.
     * @return A new instance of fragment CongratsCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CongratsCardFragment newInstance(Result result) {
        CongratsCardFragment fragment = new CongratsCardFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULT, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            result = (Result) getArguments().getSerializable(RESULT);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_congrats_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView message = view.findViewById(R.id.congratsTextMessage);

        String messageTitle = "";
        int colorBorder = 0, iconCongrats = 0;

        if (result.winner() != EPlayer.NONE) {
            messageTitle = result.getResult().toUpperCase();
            colorBorder = R.color.green;
            iconCongrats = R.drawable.asset_icon_simple_win;
        } else {
            messageTitle = result.getResult().toUpperCase();
            colorBorder = R.color.light_gray;
            iconCongrats = R.drawable.asset_icon_simple_draw;
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