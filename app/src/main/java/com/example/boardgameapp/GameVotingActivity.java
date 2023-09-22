package com.example.boardgameapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GameVotingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameVotingAdapter adapter;
    private ArrayList<GameItem> gameVoting; // Verwenden Sie GameItem-Objekte
    private List<GameItem> gameList = new ArrayList<>(); // Verwenden Sie GameItem-Objekte
    private OkHttpClient client;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_voting);

        client = new OkHttpClient();

        recyclerView = findViewById(R.id.votingGamesRecyclerView);
        submitButton = findViewById(R.id.submitButton);

        gameVoting = new ArrayList<>();
        adapter = new GameVotingAdapter(gameVoting);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnVoteClickListener(new GameVotingAdapter.OnVoteClickListener() {
            @Override
            public void onVoteClick(int position) {

                GameItem selectedGame = gameVoting.get(position);
                showToast("Spiel ausgewählt: " + selectedGame.getGameName());
                incrementVotesInDatabase(selectedGame);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<GameItem> selectedGames = adapter.getSelectedGames();
                if (!selectedGames.isEmpty()) {
                    for (GameItem gameItem : selectedGames) {
                        gameItem.setVotes(gameItem.getVotes() + 1);
                    }
                    adapter.notifyDataSetChanged();
                    sendVotesToServer(selectedGames);
                } else {
                    showToast("Bitte wählen Sie Spiele aus.");
                }
            }
        });

        loadGamesFromServer();
    }


    private void incrementVotesInDatabase(GameItem selectedGame) {
        String serverURL = "https://qu-iu-zz.beyer-its.de/update_votes.php"; // Ändern Sie die URL entsprechend Ihrer API

        String gameName = selectedGame.getGameName(); // Game-Name als Schlüssel verwenden
        int currentVotes = selectedGame.getVotes(); // Aktuelle Anzahl der Votes

        // Erhöhen Sie die Votes lokal
        selectedGame.setVotes(currentVotes + 1);
        adapter.notifyDataSetChanged(); // Aktualisieren Sie die RecyclerView-Ansicht

        // Senden Sie die aktualisierten Daten an den Server
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("Game", gameName) // Verwenden Sie den richtigen Schlüssel, falls erforderlich
                .add("Votes", String.valueOf(currentVotes + 1)) // Erhöhen Sie die Anzahl der Votes
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Fehler beim Aktualisieren der Votes auf dem Server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showToast("Votes erfolgreich aktualisiert auf dem Server");
                        // Hier können Sie weitere Aktionen durchführen, z.B. die Ansicht aktualisieren
                    });
                } else {
                    runOnUiThread(() -> {
                        showToast("Fehler beim Aktualisieren der Votes auf dem Server");
                    });
                }
            }
        });
    }




    private void loadGamesFromServer() {
        String serverURL = "https://qu-iu-zz.beyer-its.de/select_game.php";

        Request request = new Request.Builder()
                .url(serverURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(GameVotingActivity.this, "Fehler beim Abrufen der Spiele", Toast.LENGTH_SHORT).show();
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
                            String gameName = jsonObject.getString("Game");
                            int votes = jsonObject.getInt("Votes");

                            // Erstellen Sie ein GameItem-Objekt und fügen Sie es zur Liste hinzu
                            GameItem gameItem = new GameItem(gameName, votes);
                            gameList.add(gameItem);
                        }

                        runOnUiThread(() -> {
                            gameVoting.clear();
                            gameVoting.addAll(gameList);
                            adapter.notifyDataSetChanged();
                            showToast("Spielliste geladen");
                            Log.d("Debug", "Anzahl der Spiele in GameVoting: " + gameVoting.size());
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(GameVotingActivity.this, "Fehler beim Abrufen der Spiele", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void sendVotesToServer(List<GameItem> gameItems) {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/update_votes.php";

        JSONArray jsonArray = new JSONArray();

        // Konvertieren Sie die Liste von GameItem-Objekten in ein JSON-Array
        for (GameItem gameItem : gameItems) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Game", gameItem.getGameName());
                jsonObject.put("Votes", gameItem.getVotes());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Erstellen Sie eine Anfrage mit dem JSON-Array als RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(GameVotingActivity.this, "Fehler beim Senden der Votes", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(GameVotingActivity.this, "Votes erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(GameVotingActivity.this, "Fehler beim Speichern der Votes", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


}
