package com.example.boardgameapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class GameVotingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameVotingAdapter adapter;
    private ArrayList<String> suggestedGames;
    private HashMap<String, Integer> votingResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_voting);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestedGames = new ArrayList<>();
        suggestedGames.add("Spiel 1");
        suggestedGames.add("Spiel 2");
        suggestedGames.add("Spiel 3");

        votingResults = new HashMap<>();
        for (String game : suggestedGames) {
            votingResults.put(game, 0);
        }

        adapter = new GameVotingAdapter(suggestedGames, votingResults);
        recyclerView.setAdapter(adapter);

        Button voteButton = findViewById(R.id.voteButton);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Speichere das Abstimmungsergebnis
                int selectedPosition = adapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    String selectedGame = suggestedGames.get(selectedPosition);
                    int votes = votingResults.get(selectedGame);
                    votingResults.put(selectedGame, votes + 1);
                    adapter.notifyDataSetChanged();
                    showToast("Du hast für " + selectedGame + " gestimmt.");
                } else {
                    showToast("Bitte wähle ein Spiel aus, für das du stimmen möchtest.");
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
