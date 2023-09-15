package com.example.boardgameapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

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
        foodRatingBar = findViewById(R.id.foodRatingBar);
        overallRatingBar = findViewById(R.id.overallRatingBar);
        submitButton = findViewById(R.id.submitButton);

        appDatabase = AppDatabase.getInstance(this);

        submitButton.setOnClickListener(v -> saveFeedback());
    }
    private void saveFeedback() {
        float hostRating = hostRatingBar.getRating();
        float foodRating = foodRatingBar.getRating();
        float overallRating = overallRatingBar.getRating();

        Feedback feedback = new Feedback();
        feedback.setHostRating(hostRating);
        feedback.setFoodRating(foodRating);
        feedback.setOverallRating(overallRating);

        AppExecutor.getInstance().diskIO().execute(() -> {
            appDatabase.feedbackDao().insertFeedback(feedback);
            float averageRating = calculateAverageRating();
            runOnUiThread(() -> {
                showAverageRating(averageRating);
                Toast.makeText(this, "Feedback gespeichert", Toast.LENGTH_SHORT).show();
            });
        });
    }
    private void showAverageRating(float averageRating) {
        TextView averageRatingTextView = findViewById(R.id.averageRatingTextView);
        String formattedRating = String.format("%.1f", averageRating);
        averageRatingTextView.setText("Durchschnittliche Bewertung: " + formattedRating);
    }


    private float calculateAverageRating() {
        List<Feedback> allFeedback = appDatabase.feedbackDao().getAllFeedback();

        if (allFeedback.isEmpty()) {
            return 0.0f; // Keine Bewertungen vorhanden, Durchschnitt = 0
        }

        float totalRating = 0.0f;
        for (Feedback feedback : allFeedback) {
            totalRating += feedback.getHostRating();
        }

        return totalRating / allFeedback.size();
    }

}
