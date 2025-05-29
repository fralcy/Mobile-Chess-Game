package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.os.Bundle;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.interfaces.ILocalMatchViewModel;

public class LocalMatchActivity extends Activity implements ILocalMatchViewModel {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_match);
    }
}
