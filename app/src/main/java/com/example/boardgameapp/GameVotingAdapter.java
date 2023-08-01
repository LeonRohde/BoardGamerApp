package com.example.boardgameapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;


public class GameVotingAdapter extends RecyclerView.Adapter<GameVotingAdapter.ViewHolder> {

    private ArrayList<String> suggestedGames;
    private HashMap<String, Integer> votingResults;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public GameVotingAdapter(ArrayList<String> suggestedGames, HashMap<String, Integer> votingResults) {
        this.suggestedGames = suggestedGames;
        this.votingResults = votingResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_voting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String game = suggestedGames.get(position);
        holder.gameRadioButton.setText(game);
        holder.voteRadioGroup.setOnCheckedChangeListener(null); // Avoid triggering unwanted event
        holder.voteRadioGroup.clearCheck();
        holder.voteRadioGroup.check(selectedPosition == position ? holder.gameRadioButton.getId() : -1);
        holder.voteCount.setText("Stimmen: " + votingResults.get(game));
    }

    @Override
    public int getItemCount() {
        return suggestedGames.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton gameRadioButton;
        private RadioGroup voteRadioGroup;
        private TextView voteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameRadioButton = itemView.findViewById(R.id.gameRadioButton);
            voteRadioGroup = itemView.findViewById(R.id.voteRadioGroup);
            voteCount = itemView.findViewById(R.id.voteCount);

            voteRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    selectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
