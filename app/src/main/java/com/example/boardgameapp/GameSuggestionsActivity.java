package com.example.boardgameapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GameSuggestionsActivity extends AppCompatActivity {

    private EditText gameInput;
    private Button submitButton;
    private RecyclerView recyclerView;
    private SuggestedGamesAdapter adapter;
    private ArrayList<String> suggestedGames;
    private List<String> gameList = new ArrayList<>();
    private OkHttpClient client;
    SuggestedGamesAdapter SuggestedGamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_suggestions);

        SuggestedGamesAdapter = new SuggestedGamesAdapter(new ArrayList<String>());
        client = new OkHttpClient();

        gameInput = findViewById(R.id.gameInput);
        submitButton = findViewById(R.id.submitButton);
        recyclerView = findViewById(R.id.suggestedGamesRecyclerView);

        suggestedGames = new ArrayList<>();
        adapter = new SuggestedGamesAdapter((ArrayList<String>) suggestedGames);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadGamesFromServer();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String game = gameInput.getText().toString();
                if (!game.isEmpty()) {
                    sendSuggestedGameToServer(game);
                    gameInput.setText("");
                } else {

                    showToast("Bitte geben Sie ein Spiel ein.");
                }
            }
        });
    }


    private void sendSuggestedGameToServer(String game) {
        String serverURL = "https://qu-iu-zz.beyer-its.de/ins_game.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("game", game)
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Fehler beim Senden des Spiels an den Server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showToast("Spiel erfolgreich an den Server gesendet");
                        suggestedGames.add(game);
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() -> {
                        showToast("Fehler beim Senden des Spiels an den Server");
                    });
                }
            }
        });
    }

    private void loadGamesFromServer() {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/select_game.php"; // Deine Server-URL hier

        Request request = new Request.Builder()
                .url(serverURL)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(GameSuggestionsActivity.this, "Fehler beim Abrufen der Spiele", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("ServerResponse", responseData);

                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        gameList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String game = jsonObject.getString("Game");
                            gameList.add(game);
                        }

                        runOnUiThread(() -> {
                            suggestedGames.clear();
                            suggestedGames.addAll(gameList);
                            adapter.notifyDataSetChanged();
                        });
                    showToast("Spielliste geladen");
                    Log.d("Debug", "Anzahl der Spiele in suggestedGames: " + suggestedGames.size());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(GameSuggestionsActivity.this, "Fehler beim Abrufen der Spiele", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void removeSuggestedGame(int position) {
        String gameToRemove = suggestedGames.get(position);
        String serverURL = "https://qu-iu-zz.beyer-its.de/delete_game.php"; // Ersetze dies durch deine Server-URL

        RequestBody requestBody = new FormBody.Builder()
                .add("game", gameToRemove)
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Fehler beim Löschen des Spiels vom Server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        suggestedGames.remove(position);
                        adapter.notifyItemRemoved(position);
                        showToast("Spiel erfolgreich vom Server gelöscht");
                    });
                } else {
                    runOnUiThread(() -> {
                        showToast("Fehler beim Löschen des Spiels vom Server");
                    });
                }
            }
        });
    }
}
