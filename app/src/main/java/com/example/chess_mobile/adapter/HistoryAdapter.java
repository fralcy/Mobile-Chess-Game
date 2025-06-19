package com.example.chess_mobile.adapter;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.dto.response.MatchHistory;

import java.util.List;
import com.example.chess_mobile.R;
import com.example.chess_mobile.model.player.Player;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<MatchHistory> matchHistories;
    public HistoryAdapter(List<MatchHistory> matchHistories) {
        this.matchHistories=matchHistories;
    }


  




    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView matchDate, playerBlack, playerWhite, blackRes, whiteRes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.matchDate= itemView.findViewById(R.id.tvDate);
            playerBlack= itemView.findViewById(R.id.tvBlackPlayer);
            playerWhite= itemView.findViewById(R.id.tvWhitePlayer);
            blackRes=itemView.findViewById(R.id.tvBlackResult);
            whiteRes=itemView.findViewById(R.id.tvWhiteResult);
        }
    }
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_history_chess, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }
    public void setWinAndLoseText(String whiteRes, String blackRes, int whiteColor, int blackColor, HistoryAdapter.ViewHolder holder) {
        holder.whiteRes.setText(whiteRes);
        holder.whiteRes.setTextColor(whiteColor);
        holder.blackRes.setText(blackRes);
        holder.blackRes.setTextColor(blackColor);
    }
    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        MatchHistory matchHistory = matchHistories.get(position);
        if(matchHistory.getMatchState().equals("WHITE_WIN")) {
            setWinAndLoseText("WIN","LOSE",Color.GREEN, Color.RED,holder);
        }
        else if(matchHistory.getMatchState().equals("BLACK_WIN")) {
            setWinAndLoseText("LOSE","WIN",Color.RED, Color.GREEN,holder);
        }
        else {
            setWinAndLoseText("DRAW","DRAW",Color.WHITE, Color.WHITE,holder);
        }
       holder.matchDate.setText(matchHistory.getMatchTime().toString());
       holder.playerWhite.setText(matchHistory.getPlayerWhite().getPlayerName());
       holder.playerBlack.setText(matchHistory.getPlayerBlack().getPlayerName());
    }
    @Override
    public int getItemCount() {
        return matchHistories.size();
    }
}
