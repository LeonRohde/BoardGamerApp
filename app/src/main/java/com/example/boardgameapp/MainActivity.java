package com.example.boardgameapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {



    private TextView dateTextView;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       Button hostRotationButton = findViewById(R.id.hostRotationButton);
        hostRotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigieren Sie zur HostRotationActivity, wenn der Button geklickt wird
                Intent intent = new Intent(MainActivity.this, HostActivityRotation.class);
                startActivity(intent);
            }
        });

        Button gameSuggestionsActivityButton = findViewById(R.id.gameSuggestionsActivityButton);
        gameSuggestionsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigieren Sie zur gameSuggestionActivity, wenn der Button geklickt wird
                Intent intent = new Intent(MainActivity.this, GameSuggestionsActivity.class);
                startActivity(intent);
            }
        });

        Button gameVotingActivityButton = findViewById(R.id.gameVotingActivityButton);
        gameVotingActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigieren Sie zur gameVotingActivity, wenn der Button geklickt wird
                Intent intent = new Intent(MainActivity.this, GameVotingActivity.class);
                startActivity(intent);
            }
        });

        Button feedbackActivityButton = findViewById(R.id.feedbackActivityButton);
        feedbackActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigieren Sie zur feedbackActivity, wenn der Button geklickt wird
                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        Button messageActivityButton = findViewById(R.id.messageActivityButton);
        messageActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigieren Sie zur messageActivity, wenn der Button geklickt wird
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });

//      NUR FÜR TEST. WIRD SPÄTER WIEDER GELÖSCHT!!!
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigieren Sie zur loginActivity, wenn der Button geklickt wird
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // Verknüpfe die TextViews mit den Layout-Elementen
        dateTextView = findViewById(R.id.dateTextView);
        locationTextView = findViewById(R.id.locationTextView);

        // Hier rufen wir die Methode auf, um die Daten des nächsten Spieltermins zu laden
        Spieltermin nextSpielTermin = getNextSpielTermin();

        // Wenn ein nächster Spieltermin vorhanden ist, aktualisieren wir die TextViews
        if (nextSpielTermin != null) {
            dateTextView.setText("Datum: " + nextSpielTermin.getDate());
            locationTextView.setText("Ort: " + nextSpielTermin.getLocation());
        }
    }

    // Dummy-Methode, um die Daten des nächsten Spieltermins zu erhalten
    private Spieltermin getNextSpielTermin() {
        // Hier würden Sie normalerweise die Daten aus der Datenbank oder einem Backend abrufen
        // In diesem Beispiel geben wir statische Dummy-Daten zurück
        return new Spieltermin("2023-08-15", "Beispielplatz 123");
    }

}
