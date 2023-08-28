package com.example.boardgameapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private Button voteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_voting);

        initializeViews();
        initializeDatabase();

        suggestedGames = getIntent().getStringArrayListExtra("suggestedGames");

        initializeRecyclerView();
        initializeVoteButton();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        voteButton = findViewById(R.id.voteButton);
    }

    private void initializeDatabase() {
        databaseHelper = new VotingDatabaseHelper(this);
    }

    private void initializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameVotingAdapter(suggestedGames, position -> updateVotes(suggestedGames.get(position)));
        recyclerView.setAdapter(adapter);
    }

    private void initializeVoteButton() {
        voteButton.setOnClickListener(v -> {
            saveVotesToDatabase();
            showToast("Abstimmungsergebnisse wurden gespeichert.");
        });
    }

    private void updateVotes(String game) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VotingDatabaseHelper.COLUMN_GAME, game);
        values.put(VotingDatabaseHelper.COLUMN_VOTES, getVotes(db, game) + 1);

        Log.d("GameVotingActivity", "Updating votes for game: " + game);

        databaseHelper.updateVotes(db, game, values);
        db.close();
        adapter.notifyDataSetChanged();
        showToast("Du hast für " + game + " gestimmt.");
    }

    private int getVotes(SQLiteDatabase db, String game) {
        String[] projection = {VotingDatabaseHelper.COLUMN_VOTES};
        String selection = VotingDatabaseHelper.COLUMN_GAME + " = ?";
        String[] selectionArgs = {game};

        try (Cursor cursor = db.query(
                VotingDatabaseHelper.TABLE_VOTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null)) {

            int votes = 0;
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(VotingDatabaseHelper.COLUMN_VOTES);
                if (columnIndex >= 0) {
                    votes = cursor.getInt(columnIndex);
                }
            }
            return votes;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void saveVotesToDatabase() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String newTable = "CREATE TABLE IF NOT EXISTS voting_results (" +
                VotingDatabaseHelper.COLUMN_GAME + " TEXT PRIMARY KEY, " +
                VotingDatabaseHelper.COLUMN_VOTES + " INTEGER)";
        db.execSQL(newTable);

        String copyVotes = "INSERT INTO voting_results (" +
                VotingDatabaseHelper.COLUMN_GAME + ", " +
                VotingDatabaseHelper.COLUMN_VOTES + ") SELECT " +
                VotingDatabaseHelper.COLUMN_GAME + ", " +
                VotingDatabaseHelper.COLUMN_VOTES + " FROM " +
                VotingDatabaseHelper.TABLE_VOTES;
        db.execSQL(copyVotes);

        String deleteVotes = "DELETE FROM " + VotingDatabaseHelper.TABLE_VOTES;
        db.execSQL(deleteVotes);

        db.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
