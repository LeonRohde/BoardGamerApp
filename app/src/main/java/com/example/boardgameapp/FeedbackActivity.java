package com.example.boardgameapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar hostRatingBar;
    private RatingBar foodRatingBar; // Stellen Sie sicher, dass die Deklaration vorhanden ist
    private RatingBar overallRatingBar;
    private Button submitButton;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        hostRatingBar = findViewById(R.id.hostRatingBar);
        submitButton = findViewById(R.id.submitButton);

        appDatabase = AppDatabase.getInstance(this);

        submitButton.setOnClickListener(v -> saveFeedback());
    }

    private void saveFeedback() {
    }


    // Weitere Code hier ...

}
