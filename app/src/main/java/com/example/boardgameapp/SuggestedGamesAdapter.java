package com.example.boardgameapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestedGamesAdapter extends RecyclerView.Adapter<SuggestedGamesAdapter.ViewHolder> {

    private ArrayList<String> suggestedGames;
    private OnDeleteClickListener onDeleteClickListener;

    public SuggestedGamesAdapter(ArrayList<String> suggestedGames) {
        this.suggestedGames = suggestedGames;
    }

    public void removeGame(int position) {
        suggestedGames.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View gameView = inflater.inflate(R.layout.item_suggested_game, parent, false);
        return new ViewHolder(gameView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String game = suggestedGames.get(holder.getAdapterPosition());
        holder.gameTextView.setText(game);


    }



    @Override
    public int getItemCount() {
        return suggestedGames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView gameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            gameTextView = itemView.findViewById(R.id.gameTextView);
        }
    }
}
