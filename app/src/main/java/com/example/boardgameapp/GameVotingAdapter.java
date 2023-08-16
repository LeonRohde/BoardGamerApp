package com.example.boardgameapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameVotingAdapter extends RecyclerView.Adapter<GameVotingAdapter.ViewHolder> {

    private ArrayList<String> suggestedGames;
    private OnItemClickListener clickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public GameVotingAdapter(ArrayList<String> suggestedGames, OnItemClickListener clickListener) {
        this.suggestedGames = suggestedGames;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_voting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String gameName = suggestedGames.get(position);
        holder.bind(gameName, position);
    }

    @Override
    public int getItemCount() {
        return suggestedGames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button gameButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameButton = itemView.findViewById(R.id.gameButton);

            gameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                        clickListener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(String gameName, int position) {
            gameButton.setText(gameName);
            gameButton.setSelected(selectedPosition == position);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
