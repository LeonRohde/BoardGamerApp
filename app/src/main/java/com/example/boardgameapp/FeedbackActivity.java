package com.example.boardgameapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar hostRatingBar;
    private RatingBar foodRatingBar;
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
        float hostRating = hostRatingBar.getRating();

        Feedback feedback = new Feedback();
        feedback.setHostRating(hostRating);

        AppExecutor.getInstance().diskIO().execute(() -> {
            appDatabase.feedbackDao().insertFeedback(feedback);
            runOnUiThread(() -> {
                Toast.makeText(this, "Feedback gespeichert", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
