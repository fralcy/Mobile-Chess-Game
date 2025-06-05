package com.example.chess_mobile.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.player.Player;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<Player> players;

    public LeaderboardAdapter(List<Player> players) {
        this.players = players;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRank, textName, textScoreWinRate;

        public ViewHolder(View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.textRank);
            textName = itemView.findViewById(R.id.textName);
            textScoreWinRate = itemView.findViewById(R.id.textScoreWinRate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.textRank.setText(String.valueOf(position + 1));
        holder.textName.setText(player.getPlayerName());
        holder.textScoreWinRate.setText("Điểm: " + player.getScore() + " • Tỉ lệ thắng: 0%");
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}

