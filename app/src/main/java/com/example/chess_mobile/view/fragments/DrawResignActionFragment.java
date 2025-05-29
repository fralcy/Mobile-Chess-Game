package com.example.chess_mobile.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view.interfaces.DrawResignActionListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrawResignActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrawResignActionFragment extends Fragment {
    DrawResignActionListener actionListener;

    public DrawResignActionFragment() {
        // Required empty public constructor
    }

    public static DrawResignActionFragment newInstance() {
        return new DrawResignActionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.actionButtonDraw).setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDrawOffered();
        });

        view.findViewById(R.id.actionButtonResign).setOnClickListener(v -> {
            if (actionListener != null) actionListener.onResigned();
        });

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draw_resign_action, container, false);
    }

    public void setActionListener(
            DrawResignActionListener actionListener
    ) {
        this.actionListener = actionListener;
    }
}