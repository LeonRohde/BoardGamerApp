package com.example.boardgameapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class GameSuggestionsActivity extends AppCompatActivity {

    private EditText gameInput;
    private Button submitButton;
    private ArrayList<String> suggestedGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_suggestions);

        gameInput = findViewById(R.id.gameInput);
        submitButton = findViewById(R.id.submitButton);
        suggestedGames = new ArrayList<>();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Füge das vorgeschlagene Spiel der Liste hinzu
                String game = gameInput.getText().toString();
                if (!game.isEmpty()) {
                    suggestedGames.add(game);
                    gameInput.setText("");
                    showToast("Spiel vorgeschlagen: " + game);
                } else {
                    showToast("Bitte geben Sie ein Spiel ein.");
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
