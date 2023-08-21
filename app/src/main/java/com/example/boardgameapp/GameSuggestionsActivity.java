package com.example.boardgameapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GameSuggestionsActivity extends AppCompatActivity {

    private EditText gameInput;
    private Button submitButton;
    private VotingDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_suggestions);

        databaseHelper = new VotingDatabaseHelper(this);

        gameInput = findViewById(R.id.gameInput);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String game = gameInput.getText().toString();
                if (!game.isEmpty()) {
                    addSuggestedGameToDatabase(game); // Füge das vorgeschlagene Spiel zur Datenbank hinzu
                    gameInput.setText("");
                    showToast("Spiel vorgeschlagen: " + game);
                } else {
                    showToast("Bitte geben Sie ein Spiel ein.");
                }
            }
        });
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
}
