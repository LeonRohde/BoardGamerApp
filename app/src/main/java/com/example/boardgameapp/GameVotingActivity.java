package com.example.boardgameapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameVotingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameVotingAdapter adapter;
    private ArrayList<String> suggestedGames;
    private VotingDatabaseHelper databaseHelper;
    private Button voteButton; // Button zum Speichern der Abstimmungsergebnisse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_voting);

        databaseHelper = new VotingDatabaseHelper(GameVotingActivity.this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Empfange die Liste der vorgeschlagenen Spiele aus dem Intent
        suggestedGames = getIntent().getStringArrayListExtra("suggestedGames");

        adapter = new GameVotingAdapter(suggestedGames, new GameVotingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                updateVotes(suggestedGames.get(position));
            }
        });
        recyclerView.setAdapter(adapter);

        voteButton = findViewById(R.id.voteButton); // Button initialisieren
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVotesToDatabase(); // Bei Klick auf den Button Abstimmungsergebnisse speichern
                showToast("Abstimmungsergebnisse wurden gespeichert.");
            }
        });
    }

    private void updateVotes(String game) {
        VotingDatabaseHelper db = new VotingDatabaseHelper(GameVotingActivity.this);
        ContentValues values = new ContentValues();
        values.put(VotingDatabaseHelper.COLUMN_GAME, game);
        values.put(VotingDatabaseHelper.COLUMN_VOTES, getVotes(game) + 1);

        // Log the game value
        Log.d("GameVotingActivity", "Updating votes for game: " + game);

        db.onVoting(game);
        db.close();
        adapter.notifyDataSetChanged();
        showToast("Du hast für " + game + " gestimmt.");
    }

    private int getVotes(String game) {
        VotingDatabaseHelper db = new VotingDatabaseHelper(GameVotingActivity.this);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();

        String[] projection = {VotingDatabaseHelper.COLUMN_VOTES};
        String selection = VotingDatabaseHelper.COLUMN_GAME + " = ?";
        String[] selectionArgs = {game};

        Cursor cursor = readableDatabase.query(
                VotingDatabaseHelper.TABLE_VOTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int votes = 0;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(VotingDatabaseHelper.COLUMN_VOTES);
            if (columnIndex >= 0) {
                votes = cursor.getInt(columnIndex);
            }
        }

        cursor.close();
        db.close();
        return votes;
    }

    private void saveVotesToDatabase() {
        VotingDatabaseHelper db = new VotingDatabaseHelper(GameVotingActivity.this);
        SQLiteDatabase writableDatabase = db.getWritableDatabase();
        String newTable = "CREATE TABLE IF NOT EXISTS voting_results (" +
                VotingDatabaseHelper.COLUMN_GAME + " TEXT PRIMARY KEY, " +
                VotingDatabaseHelper.COLUMN_VOTES + " INTEGER)";
        writableDatabase.execSQL(newTable);

        String copyVotes = "INSERT INTO voting_results (" +
                VotingDatabaseHelper.COLUMN_GAME + ", " +
                VotingDatabaseHelper.COLUMN_VOTES + ") SELECT " +
                VotingDatabaseHelper.COLUMN_GAME + ", " +
                VotingDatabaseHelper.COLUMN_VOTES + " FROM " +
                VotingDatabaseHelper.TABLE_VOTES;
        writableDatabase.execSQL(copyVotes);

        String deleteVotes = "DELETE FROM " + VotingDatabaseHelper.TABLE_VOTES;
        writableDatabase.execSQL(deleteVotes);

        db.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
