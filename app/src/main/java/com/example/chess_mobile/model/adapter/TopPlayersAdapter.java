package com.example.chess_mobile.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess_mobile.R;

import java.util.List;

public class TopPlayersAdapter extends RecyclerView.Adapter<TopPlayersAdapter.PlayerViewHolder> {

    private final List<String> players;

    public TopPlayersAdapter(List<String> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        holder.playerTextView.setText(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerTextView;

        PlayerViewHolder(View itemView) {
            super(itemView);
            playerTextView = itemView.findViewById(R.id.topPlayerText);
        }
    }
}
