package com.example.boardgameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private SpielterminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    Log.d("FirebaseToken", idToken);
                } else {
                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        });

        // Initialisiere die RecyclerView und den Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SpielterminAdapter(new ArrayList<Spieltermin>());
        recyclerView.setAdapter(adapter);

        // Die Buttons wurden korrigiert und die Funktionalität bleibt erhalten
        Button gameSuggestionsActivityButton = findViewById(R.id.gameSuggestionsActivityButton);
        gameSuggestionsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameSuggestionsActivity.class);
                startActivity(intent);
            }
        });

        Button gameVotingActivityButton = findViewById(R.id.gameVotingActivityButton);
        gameVotingActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameVotingActivity.class);
                startActivity(intent);
            }
        });

        Button feedbackActivityButton = findViewById(R.id.feedbackActivityButton);
        feedbackActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        Button messageActivityButton = findViewById(R.id.messageActivityButton);
        messageActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "Erfolgreich abgemeldet",
                                        Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        });
            }
        });

        // Hier rufen wir die Methode auf, um die Daten aus der Webdatenbank abzurufen
        getDataFromServer();
    }

    private void getDataFromServer() {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/getGameData.php";

        Request request = new Request.Builder()
                .url(serverURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Fehlerbehandlung für den Fall, dass die Anfrage fehlschlägt
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Fehler beim Herunterladen der Daten", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        final List<Spieltermin> spieltermine = parseJSON(responseData);

                        // Aktualisieren Sie die RecyclerView auf dem UI-Thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(spieltermine);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Fehlerbehandlung für ungültiges JSON
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Fehler beim Verarbeiten der Daten", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Fehlerbehandlung, wenn die Anfrage nicht erfolgreich war
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Fehler beim Herunterladen der Daten", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private List<Spieltermin> parseJSON(String jsonData) throws JSONException {
        List<Spieltermin> spieltermine = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonData);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String vorname = jsonObject.getString("Vorname");
            String ort = jsonObject.getString("Ort");
            String spieldt = jsonObject.getString("spieldt");
            Spieltermin spieltermin = new Spieltermin(vorname, ort, spieldt); // Passen Sie den Konstruktor von Spieltermin an
            spieltermine.add(spieltermin);
        }

        return spieltermine;
    }

}
