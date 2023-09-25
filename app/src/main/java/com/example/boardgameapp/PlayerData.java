package com.example.boardgameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayerData extends AppCompatActivity {
        private static String TAG = "PlayerData";
        private Button btnUpdAbbr, btnUpdSichern;
        private EditText updVorname, updName, updStrasse, updHausnr, updPLZ, updOrt, updTermin;
        private String userMail, hostMail, reqMail;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_player_data);

                updVorname = findViewById(R.id.updVorname);
                updName = findViewById(R.id.updName);
                updStrasse = findViewById(R.id.updStrasse);
                updHausnr = findViewById(R.id.updHausnr);
                updPLZ = findViewById(R.id.updPLZ);
                updOrt = findViewById(R.id.updOrt);
                updTermin = findViewById(R.id.updTermin);

                //Aus MainActivity übergebene Mail ermitteln
                Intent intent = getIntent();
                Bundle inhalt = intent.getExtras();
                hostMail = inhalt.getString("email");

                //Angemeldete Mail ermitteln
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userMail = user.getEmail();

                //Email für Tabellenzugriffe belegen
                reqMail = userMail;

                //Wenn die Mails nicht identisch sind, darf nur der Spieltermin verändert werden
                if (!hostMail.equals(userMail)){
                        reqMail = hostMail;
                        updVorname.setEnabled(false);
                        updName.setEnabled(false);
                        updStrasse.setEnabled(false);
                        updHausnr.setEnabled(false);
                        updHausnr.setEnabled(false);
                        updPLZ.setEnabled(false);
                        updOrt.setEnabled(false);
                }

                getPlayerData(hostMail);

                btnUpdAbbr = findViewById(R.id.btnUpdAbbr);
                btnUpdAbbr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                PlayerData.super.onBackPressed();
                        }
                });

                btnUpdSichern = findViewById(R.id.btnUpdSichern);
                btnUpdSichern.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                                updSpieler();
                                finish();
                                Intent back = new Intent(PlayerData.this, MainActivity.class);
                                startActivity(back);
                        }
                });
        }

        private void updSpieler() {
                String strasse, hausnr, plz, ort, spieldt;

                strasse = updStrasse.getText().toString();
                hausnr = updHausnr.getText().toString();
                plz = updPLZ.getText().toString();
                ort = updOrt.getText().toString();
                spieldt = updTermin.getText().toString();

                OkHttpClient client = new OkHttpClient();
                String postPlayerUrl = "https://qu-iu-zz.beyer-its.de/update_player.php";

                RequestBody reqBody = new FormBody.Builder()
                        .add("strasse", strasse)
                        .add("hausnr", hausnr)
                        .add("plz", plz)
                        .add("ort", ort)
                        .add("spieldt", spieldt)
                        .add("email", reqMail)
                        .build();
                Log.d(TAG, "Request: " + reqBody);

                Request request = new Request.Builder().url(postPlayerUrl).post(reqBody).build();

                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.d(TAG, "Fehler beim Update: Player: " + e.getMessage());

                                Toast.makeText(PlayerData.this, "Spieler hinzufügen fehlgeschlagen",
                                        Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                try {
                                        String myResp = response.body().string();

                                        if (response.isSuccessful()){
                                                Log.d(TAG, "Spieler erfolgreich aktualisiert");
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                                Toast.makeText(PlayerData.this,
                                                                        "Daten wurden erfolgreich aktualisiert.",
                                                                        Toast.LENGTH_SHORT).show();
                                                        }
                                                });
                                        }else {
                                                Log.d(TAG, "Spieler konnte nicht aktualisiert werden");
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                                Toast.makeText(PlayerData.this,
                                                                        "Daten konnten nicht aktualisiert werden.",
                                                                        Toast.LENGTH_SHORT).show();
                                                        }
                                                });
                                        }

                                }catch (IOException e) {
                                        Log.d(TAG, "Exception: " + e.getMessage());
                                }
                                response.close();
                        }
                });
                client.connectionPool().evictAll();
        }

        private void getPlayerData(String hostMail) {
                OkHttpClient client = new OkHttpClient();
                String serverURL = "https://qu-iu-zz.beyer-its.de/select_player.php?email=" + reqMail;

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
                                                Toast.makeText(PlayerData.this,
                                                        "Fehler beim Herunterladen der Daten",
                                                        Toast.LENGTH_SHORT).show();
                                        }
                                });
                                Log.d(TAG, "Fehler bei SQL: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                        try {
                                                String responseData = response.body().string();
                                                JSONArray playerArray = new JSONArray(responseData);
                                                JSONObject playerdaten = playerArray.getJSONObject(0);

                                                String jsonVorname = playerdaten.getString("vorname");
                                                String jsonName = playerdaten.getString("name");
                                                String jsonStrasse = playerdaten.getString("strasse");
                                                String jsonHausnr = playerdaten.getString("hausnr");
                                                String jsonPLZ = playerdaten.getString("plz");
                                                String jsonOrt = playerdaten.getString("ort");
                                                String jsonSpielDT = playerdaten.getString("spieldt");

                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                                updVorname.setText(jsonVorname);
                                                                updName.setText(jsonName);
                                                                updStrasse.setText(jsonStrasse);
                                                                updHausnr.setText(jsonHausnr);
                                                                updPLZ.setText(jsonPLZ);
                                                                updOrt.setText(jsonOrt);
                                                                updTermin.setText(jsonSpielDT);
                                                        }
                                                });

                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                                // Fehlerbehandlung für ungültiges JSON
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                                Toast.makeText(PlayerData.this,
                                                                        "Fehler beim Verarbeiten der Daten",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Log.d(TAG, "Fehler bei Verarbeitung: " +
                                                                        e.getMessage());
                                                        }
                                                });
                                        }
                                } else {
                                        // Fehlerbehandlung, wenn die Anfrage nicht erfolgreich war
                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                        Toast.makeText(PlayerData.this,
                                                                "Fehler beim Herunterladen der Daten",
                                                                Toast.LENGTH_SHORT).show();
                                                }
                                        });
                                }
                                response.close();
                        }
                });
                client.connectionPool().evictAll();
        }
}