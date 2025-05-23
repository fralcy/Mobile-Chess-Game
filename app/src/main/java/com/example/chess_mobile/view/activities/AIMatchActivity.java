package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.os.Bundle;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.IAIMatchViewModel;

public class AIMatchActivity extends Activity implements IAIMatchViewModel {
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_ai_match);
    }
}
