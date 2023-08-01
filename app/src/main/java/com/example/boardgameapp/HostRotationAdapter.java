package com.example.boardgameapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class HostRotationAdapter extends RecyclerView.Adapter<HostRotationAdapter.ViewHolder> {

    private ArrayList<Player> playerList;

    public HostRotationAdapter(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.playerNameTextView.setText(player.getName());
        holder.hostStatusTextView.setText(player.isHost() ? "Gastgeber" : "");
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView playerNameTextView;
        private TextView hostStatusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            hostStatusTextView = itemView.findViewById(R.id.hostStatusTextView);
        }
    }
}
