package com.example.chess_mobile.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.view_model.interfaces.IRankedMatchViewModel;

public class RankedMatchActivity extends Activity implements IRankedMatchViewModel {
    private Button button10min;
    private Button button15min;
    private Button button20min;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranked_match);
        bindView();
        setUpOnClick();

    }
    public void bindView() {
        button10min = findViewById(R.id.rankedMatchButton10Min);
        button15min = findViewById(R.id.rankedMatchButton15Min);
        button20min = findViewById(R.id.rankedMatchButton20Min);
    }
    private int selectedPlayTime = 10; // mặc định

    private int getSelectedPlayTime() {
        return selectedPlayTime;
    }
    public void setUpOnClick() {
        findViewById(R.id.rankedMatchButtonPlay).setOnClickListener(v->{
            Intent intent = new Intent(RankedMatchActivity.this,FindMatchActivity.class);
            intent.putExtra("Rank_Play_Time", getSelectedPlayTime());
            startActivity(intent);
        });
        button10min.setOnClickListener(v->{
            RankedMatchActivity.this.button10min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_button_bg));
            RankedMatchActivity.this.button15min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_gray_button_bg));
            RankedMatchActivity.this.button20min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_gray_button_bg));
            selectedPlayTime = 10;
        });
        button15min.setOnClickListener(v->{
            RankedMatchActivity.this.button10min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_gray_button_bg));
            RankedMatchActivity.this.button15min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_button_bg));
            RankedMatchActivity.this.button20min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_gray_button_bg));
            selectedPlayTime = 15;
        });
        button20min.setOnClickListener(v->{
            RankedMatchActivity.this.button10min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_gray_button_bg));
            RankedMatchActivity.this.button15min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_gray_button_bg));
            RankedMatchActivity.this.button20min.setBackground(ContextCompat.getDrawable(RankedMatchActivity.this,R.drawable.rounded_button_bg));
            selectedPlayTime = 20;
        });

    }


}
