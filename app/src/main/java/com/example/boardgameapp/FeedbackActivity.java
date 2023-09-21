package com.example.boardgameapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar hostRatingBar;
    private RatingBar foodRatingBar;
    private RatingBar eveningRatingBar;
    private Spinner spielerSpinner;
    private Button submitButton;

    private List<String> spielerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        hostRatingBar = findViewById(R.id.hostRatingBar);
        foodRatingBar = findViewById(R.id.foodRatingBar);
        eveningRatingBar = findViewById(R.id.eveningRatingBar);
        spielerSpinner = findViewById(R.id.spielerSpinner);
        submitButton = findViewById(R.id.submitButton);

        spielerList = new ArrayList<>();

        getSpielerFromDatabase();

        ArrayAdapter<String> spielerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spielerList);
        spielerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spielerSpinner.setAdapter(spielerAdapter);

        submitButton.setOnClickListener(v -> {
            saveFeedback();
            displayAverageRating();
        });
    }

    private void getSpielerFromDatabase() {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/select_feedback.php";

        Request request = new Request.Builder()
                .url(serverURL)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(FeedbackActivity.this, "Fehler beim Abrufen der Spieler", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    try {
                        JSONArray jsonArray = new JSONArray(responseData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String spieler = jsonArray.getString(i);
                            spielerList.add(spieler);
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> spielerAdapter = new ArrayAdapter<>(FeedbackActivity.this, android.R.layout.simple_spinner_item, spielerList);
                            spielerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spielerSpinner.setAdapter(spielerAdapter);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(FeedbackActivity.this, "Fehler beim Abrufen der Spieler", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void saveFeedback() {
        float hostRating = hostRatingBar.getRating();
        float foodRating = foodRatingBar.getRating();
        float eveningRating = eveningRatingBar.getRating();

        String selectedSpieler = spielerSpinner.getSelectedItem().toString();

        sendFeedbackToServer(selectedSpieler, hostRating, foodRating, eveningRating);
    }

    private void sendFeedbackToServer(String selectedSpieler, float hostRating, float foodRating, float eveningRating) {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/update_feedback.php";

        RequestBody requestBody = new FormBody.Builder()
                .add("Spieler", selectedSpieler)
                .add("Gastgeber", String.valueOf(hostRating))
                .add("Essen", String.valueOf(foodRating))
                .add("Abend", String.valueOf(eveningRating))
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(FeedbackActivity.this, "Fehler beim Senden des Feedbacks", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(FeedbackActivity.this, "Feedback erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(FeedbackActivity.this, "Fehler beim Speichern des Feedbacks", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void displayAverageRating() {
        float hostRating = hostRatingBar.getRating();
        float foodRating = foodRatingBar.getRating();
        float eveningRating = eveningRatingBar.getRating();

        float averageRating = (hostRating + foodRating + eveningRating) / 3;

        // Begrenze auf eine Dezimalstelle
        String formattedRating = String.format("%.1f", averageRating);

        TextView averageRatingTextView = findViewById(R.id.averageRatingTextView);
        averageRatingTextView.setText("Durchschnittliche Bewertung: " + formattedRating);
    }

}
