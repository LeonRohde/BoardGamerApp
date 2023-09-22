package com.example.boardgameapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameVotingAdapter extends RecyclerView.Adapter<GameVotingAdapter.ViewHolder> {

    private ArrayList<GameItem> gameVoting; // Verwenden Sie eine Klasse, um sowohl den Spielnamen als auch die Votes darzustellen
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnVoteClickListener onVoteClickListener;

    public GameVotingAdapter(ArrayList<GameItem> gameVoting) {
        this.gameVoting = gameVoting;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnVoteClickListener {
        void onVoteClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View gameView = inflater.inflate(R.layout.item_game_voting, parent, false);
        return new ViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GameItem gameItem = gameVoting.get(holder.getAdapterPosition());
        holder.gameTextView.setText(gameItem.getGameName());
        holder.votesTextView.setText(String.valueOf(gameItem.getVotes()));
        holder.gameCheckBox.setChecked(gameItem.isSelected());

        if (holder.getAdapterPosition() == selectedPosition) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        // Remove the existing OnClickListener
        holder.gameCheckBox.setOnClickListener(null);

        holder.gameCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.gameCheckBox.isChecked();
                gameItem.setSelected(isChecked);

                if (isChecked) {
                    selectedPosition = holder.getAdapterPosition();
                    uncheckOtherCheckBoxes(holder.getAdapterPosition()); // Uncheck other CheckBoxes
                    notifyDataSetChanged();
                } else {
                    selectedPosition = RecyclerView.NO_POSITION;
                    notifyDataSetChanged();
                }
            }
        });
    }





    @Override
    public int getItemCount() {
        return gameVoting.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView gameTextView;
        public TextView votesTextView;
        public CheckBox gameCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            gameTextView = itemView.findViewById(R.id.gameTextView);
            votesTextView = itemView.findViewById(R.id.votesTextView); // TextView für die Votes-Spalte
            gameCheckBox = itemView.findViewById(R.id.gameCheckBox);
        }
    }

    public void setOnVoteClickListener(OnVoteClickListener onVoteClickListener) {
        this.onVoteClickListener = onVoteClickListener;
    }
    private void uncheckOtherCheckBoxes(int position) {
        for (int i = 0; i < gameVoting.size(); i++) {
            if (i != position) {
                GameItem gameItem = gameVoting.get(i);
                gameItem.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }
    public ArrayList<GameItem> getSelectedGames() {
        ArrayList<GameItem> selectedGames = new ArrayList<>();
        for (GameItem gameItem : gameVoting) {
            if (gameItem.isSelected()) {
                selectedGames.add(gameItem);
            }
        }
        return selectedGames;
    }

}
