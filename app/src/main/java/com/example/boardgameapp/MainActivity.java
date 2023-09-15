package com.example.boardgameapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView dateTextView;
    private TextView locationTextView;



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

        dateTextView = findViewById(R.id.dateTextView);
        locationTextView = findViewById(R.id.locationTextView);

        Spieltermin nextSpielTermin = getNextSpielTermin();

        if (nextSpielTermin != null) {
            dateTextView.setText("Datum: " + nextSpielTermin.getDate());
            locationTextView.setText("Ort: " + nextSpielTermin.getLocation());
        }

        // Hier rufen wir die Methode auf, um die RecyclerView zu aktualisieren
        updateRecyclerView();
    }

    private List<Spieltermin> getDatenAusWebdatenbank() {
        List<Spieltermin> spieltermine = new ArrayList<>();

        try {
            String serverURL = "https://qu-iu-zz.beyer-its.de/getGameData.php";
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();

                String responseData = stringBuilder.toString();

                // Hier kannst du den JSON-String parsen und die Spieltermine erstellen
                // (ersetze es durch deinen eigenen Code)

            } else {
                // Fehlerbehandlung, wenn die Anfrage nicht erfolgreich war
            }

            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return spieltermine;
    }

    private void getDataFromFTP() {
        String server = "ftp.beyer-its.de";
        int port = 21;
        String username = "qu-iu-zz@beyer-its.de";
        String password = "=5Ap-PVCKz=yy#S7";
        String remoteFilePath = "getGameData.php";

        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();

            if (FTPReply.isPositiveCompletion(replyCode)) {
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
    }

    private Spieltermin getNextSpielTermin() {
        // Dummy-Methode, um die Daten des nächsten Spieltermins zu erhalten
        // Hier würdest du normalerweise die Daten aus der Datenbank oder einem Backend abrufen
        // In diesem Beispiel geben wir statische Dummy-Daten zurück
        return new Spieltermin("2023-08-15", "Beispielplatz 123");
    }

    private void updateRecyclerView() {
        // Hier rufst du die Daten aus der Webdatenbank ab
        // Ersetze dies durch deinen tatsächlichen Code, um die Daten abzurufen
        List<Spieltermin> spieltermine = getDatenAusWebdatenbank();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SpielterminAdapter adapter = new SpielterminAdapter(spieltermine);
        recyclerView.setAdapter(adapter);
    }

}
