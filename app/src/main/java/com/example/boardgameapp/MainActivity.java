package com.example.boardgameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SpielterminInterface {
    private static String TAG = "MainActivity";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private SpielterminAdapter adapter;
    private List<Spieltermin> spieltermine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (!task.isSuccessful()) {
                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        });

        // Initialisiere die RecyclerView und den Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SpielterminAdapter(this, new ArrayList<Spieltermin>(), this);
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
                                finish();
                                startActivity(loginIntent);
                            }
                        });
            }
        });

        // Hier rufen wir die Methode auf, um die Daten aus der Webdatenbank abzurufen
        getDataFromServer();

        //Löschen von Elementen in der RecyclerView
        ItemTouchHelper deleteHelper = new ItemTouchHelper(delete);
        deleteHelper.attachToRecyclerView(recyclerView);
    }

    private void getDataFromServer() {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/select_player.php";

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

                       // final List<Spieltermin> spieltermine = parseJSON(responseData);
                        spieltermine = parseJSON(responseData);
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
                response.close();
            }
        });
        client.connectionPool().evictAll();
    }

    private List<Spieltermin> parseJSON(String jsonData) throws JSONException {
        spieltermine = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonData);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String vorname = jsonObject.getString("vorname");
            String name = jsonObject.getString("name");
            String ort = jsonObject.getString("ort");
            String spieldt = jsonObject.getString("spieldt");
            String email = jsonObject.getString("email");
            String hostName = vorname + " " + name;

            Spieltermin spieltermin = new Spieltermin(hostName, ort, spieldt, email); // Passen Sie den Konstruktor von Spieltermin an
            spieltermine.add(spieltermin);
        }
        return spieltermine;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, PlayerData.class);
        intent.putExtra("email", spieltermine.get(position).getEmail());
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback delete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            String del = "";
            int pos = viewHolder.getAdapterPosition();
            String delEmail = spieltermine.get(pos).getEmail();

            if(pos != 0){
                Toast.makeText(MainActivity.this, "Nur der erste Eintrag in der Liste darf gelöscht werden.",
                        Toast.LENGTH_LONG).show();
                del = "Fehler";
            }

            if(!delEmail.equals(user.getEmail()) && del.isEmpty()) {
                //aktuelles Datum ermitteln
                Calendar kalender = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String curDate = dateFormat.format(kalender.getTime());

                if (spieltermine.get(pos).getSpieldt().compareTo(curDate) >= 0){
                    del = "Fehler";
                    Toast.makeText(MainActivity.this, "Löschen nicht möglich: Termin ist noch nicht abgelaufen.",
                            Toast.LENGTH_LONG).show();
                }
            }

            if (del.isEmpty()){
                spieltermine.remove(pos);
                delHostWeb(delEmail);
                getDataFromServer();
                adapter.updateData(spieltermine);
            }else{
                adapter.updateData(spieltermine);
            }
        }
    };

    private void delHostWeb(String email) {
        OkHttpClient client = new OkHttpClient();
        String serverURL = "https://qu-iu-zz.beyer-its.de/host_rotation.php?email=" + email;

        Request request = new Request.Builder()
                 .url(serverURL)
                 .build();

        client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "Fehler delete Host: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "Fehler beim Zugriff für Gastgeberwechsel.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.d(TAG, "Response: " + response.isSuccessful());
                    if (response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "Gastegeber wurde erfolgreich gewechselt.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "Gastegeber konnte nicht gewechselt werden.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
    }
}
