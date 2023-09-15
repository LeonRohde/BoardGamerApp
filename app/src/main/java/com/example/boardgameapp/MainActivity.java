package com.example.boardgameapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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

        // Hier rufen wir die Methode auf, um die RecyclerView zu aktualisieren
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // Hier starten wir einen Hintergrundthread, um die Daten aus der Webdatenbank abzurufen
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Spieltermin> spieltermine = getDatenAusWebdatenbank();

                // Die RecyclerView muss auf dem Hauptthread aktualisiert werden
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Aktualisiere den RecyclerView-Adapter mit den erhaltenen Daten
                        adapter.updateData(spieltermine);

                        // Fügen Sie Log-Nachrichten hinzu, um den Ablauf zu überprüfen
                        Log.d("MainActivity", "RecyclerView aktualisiert. Anzahl der Spieltermine: " + spieltermine.size());
                    }
                });
            }
        }).start();
    }

    private List<Spieltermin> getDatenAusWebdatenbank() {
        List<Spieltermin> spieltermine = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();

        try {
            String server = "ftp.beyer-its.de";
            int port = 21;
            String username = "qu-iu-zz@beyer-its.de";
            String password = "=5Ap-PVCKz=yy#S7";
            String remoteFilePath = "getGameData.php";

            ftpClient.connect(server, port);

            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                boolean loginSuccess = ftpClient.login(username, password);

                if (loginSuccess) {
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    String localFilePath = getFilesDir() + "/getGameData.php";
                    boolean downloadSuccess = ftpClient.retrieveFile(remoteFilePath, new FileOutputStream(localFilePath));

                    if (downloadSuccess) {
                        // Die Datei wurde erfolgreich heruntergeladen
                        // Hier kannst du den PHP-Code ausführen, um die Daten abzurufen
                    } else {
                        // Fehlerbehandlung, wenn die Datei nicht erfolgreich heruntergeladen wurde
                    }

                    ftpClient.logout();
                    ftpClient.disconnect();
                } else {
                    // Fehlerbehandlung, wenn die Anmeldung nicht erfolgreich war
                }
            } else {
                // Fehlerbehandlung, wenn keine Verbindung zum Server hergestellt werden konnte
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return spieltermine;
    }


    private List<Spieltermin> parseJSON(String jsonData) {
        List<Spieltermin> spieltermine = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("ID");
                String email = jsonObject.getString("Email");
                // Füge hier weitere Felder hinzu, die du aus dem JSON-Objekt auslesen möchtest
                // Beispiel: String vorname = jsonObject.getString("Vorname");

                // Erstelle ein Spieltermin-Objekt und füge es zur List hinzu
                Spieltermin spieltermin = new Spieltermin(id, email);
                spieltermine.add(spieltermin);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return spieltermine;
    }
}
