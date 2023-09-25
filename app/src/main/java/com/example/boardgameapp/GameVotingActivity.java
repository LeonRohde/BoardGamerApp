package com.example.boardgameapp;

import android.os.Bundle;
import android.os.Handler;
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
                incrementVotesInRecyclerView(selectedGame);
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
                    sendVotesToServer();

                    // Verzögern Sie das Laden der Spiele um 1 Sekunde
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadGamesFromServer();
                        }
                    }, 1000); // 1000 Millisekunden (1 Sekunde)
                } else {
                    showToast("Bitte wählen Sie Spiele aus.");
                }
            }
        });


        loadGamesFromServer();
    }

    private void incrementVotesInRecyclerView(GameItem selectedGame) {
        int currentVotes = selectedGame.getVotes(); // Aktuelle Anzahl der Votes

        // Erhöhen Sie die Votes lokal
        selectedGame.setVotes(currentVotes + 1);
        adapter.notifyDataSetChanged(); // Aktualisieren Sie die RecyclerView-Ansicht
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

    private void sendVotesToServer() {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/update_votes.php";

        // Erstellen Sie ein JSON-Array, um die gesamte Tabelle darzustellen
        JSONArray jsonArray = new JSONArray();

        for (GameItem gameItem : gameVoting) {
            try {
                JSONObject gameObject = new JSONObject();
                gameObject.put("Game", gameItem.getGameName());
                gameObject.put("Votes", gameItem.getVotes());
                jsonArray.put(gameObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Erstellen Sie den Anfrage-Body mit dem JSON-Array
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(GameVotingActivity.this, "Fehler beim Senden der Votes", Toast.LENGTH_SHORT).show();
                    Log.e("ServerResponse", "Fehler beim Senden der Votes", e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(GameVotingActivity.this, "Votes erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
                        Log.d("ServerResponse", "Votes erfolgreich gespeichert");
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(GameVotingActivity.this, "Fehler beim Speichern der Votes", Toast.LENGTH_SHORT).show();
                        Log.e("ServerResponse", "Fehler beim Speichern der Votes. Response Code: " + response.code());
                    });
                }
            }
        });
    }

}
