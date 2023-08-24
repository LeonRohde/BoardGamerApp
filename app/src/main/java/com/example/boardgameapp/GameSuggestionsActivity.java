package com.example.boardgameapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameSuggestionsActivity extends AppCompatActivity {

    private EditText gameInput;
    private Button submitButton;
    private RecyclerView recyclerView;
    private SuggestedGamesAdapter adapter; // Erstelle den Adapter für die RecyclerView
    private ArrayList<String> suggestedGames; // Liste der vorgeschlagenen Spiele
    private VotingDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_suggestions);

        databaseHelper = new VotingDatabaseHelper(this);

        gameInput = findViewById(R.id.gameInput);
        submitButton = findViewById(R.id.submitButton);
        recyclerView = findViewById(R.id.suggestedGamesRecyclerView);

        suggestedGames = new ArrayList<>();
        loadSuggestedGames(); // Lade vorgeschlagene Spiele aus der Datenbank

        // Erstelle den Adapter mit der Liste der vorgeschlagenen Spiele
        adapter = new SuggestedGamesAdapter(suggestedGames, new SuggestedGamesAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeSuggestedGame(position); // Aufruf der Löschfunktion
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String game = gameInput.getText().toString();
                if (!game.isEmpty()) {
                    addSuggestedGameToDatabase(game); // Füge das vorgeschlagene Spiel zur Datenbank hinzu
                    gameInput.setText("");
                    suggestedGames.add(game); // Füge das Spiel zur Liste hinzu
                    adapter.notifyDataSetChanged(); // Benachrichtige den Adapter über die Änderung
                    showToast("Spiel vorgeschlagen: " + game);
                } else {
                    showToast("Bitte geben Sie ein Spiel ein.");
                }
            }
        });
    }

    private void loadSuggestedGames() {
        VotingDatabaseHelper db = new VotingDatabaseHelper(this);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();

        Cursor cursor = readableDatabase.query(
                VotingDatabaseHelper.TABLE_VOTES,
                new String[]{VotingDatabaseHelper.COLUMN_GAME},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String game = cursor.getString(cursor.getColumnIndex(VotingDatabaseHelper.COLUMN_GAME));
                suggestedGames.add(game); // Füge das Spiel zur Liste hinzu
            } while (cursor.moveToNext());

            cursor.close();
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
    }


    private void addSuggestedGameToDatabase(String game) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VotingDatabaseHelper.COLUMN_GAME, game);
        values.put(VotingDatabaseHelper.COLUMN_VOTES, 0);

        db.insertWithOnConflict(VotingDatabaseHelper.TABLE_VOTES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void removeSuggestedGame(int position) {
        adapter.removeGame(position);
        // Hier kannst du den Eintrag auch aus der Datenbank entfernen, wenn gewünscht
    }
}
