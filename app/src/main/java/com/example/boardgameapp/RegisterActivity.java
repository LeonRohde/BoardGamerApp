package com.example.boardgameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    private final String TAG ="RegisterActivity";
    private Button btnAbbruch, btnRegist;
    private String pruefung = new String("");
    private String emailAuth, pwAuth, email, vorname, name, strasse, hausnr, plz, ort;
    private FirebaseAuth mAuth;
    private EditText eMailEdit, passwortEdit, passwortWiederEdit, vorNameEdit, nameEdit,
            strasseEdit, hausNrEdit, plzEdit, ortEdit;
    private TextView ergebnis;
    private String myResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnAbbruch = findViewById(R.id.btnAbbruch);
        btnRegist = findViewById(R.id.btnRegist);

        btnAbbruch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.super.onBackPressed();
            }
        });
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pruefenEingabe();

                if (pruefung.equals("OK")){
                    Log.d(TAG, "Prüfung erfolgreich");
                    addPlayer();
                    if (pruefung.equals("OK")) {
                        addFirebase();
                    }
                }else{
                    ergebnis.setText(pruefung);
                }
            }
        });
    }
    private void pruefenEingabe() {
        eMailEdit = (EditText) findViewById(R.id.editTextRegEmail);
        passwortEdit = (EditText) findViewById(R.id.editTextRegPW);
        passwortWiederEdit = (EditText) findViewById(R.id.editTextRegPWWieder);
        vorNameEdit = (EditText) findViewById(R.id.editTextRegVorName);
        nameEdit = (EditText) findViewById(R.id.editTextRegName);
        strasseEdit = (EditText) findViewById(R.id.editTextRegStr);
        hausNrEdit = (EditText) findViewById(R.id.editTextRegHausNr);
        plzEdit = (EditText) findViewById(R.id.editTextRegPLZ);
        ortEdit = (EditText) findViewById(R.id.editTextRegOrt);
        ergebnis = (TextView) findViewById(R.id.textPruefung);

        if(TextUtils.isEmpty(eMailEdit.getText().toString())
                || TextUtils.isEmpty(passwortEdit.getText().toString())
                || TextUtils.isEmpty(passwortWiederEdit.getText().toString())
                || TextUtils.isEmpty(vorNameEdit.getText().toString())
                || TextUtils.isEmpty(nameEdit.getText().toString())
                || TextUtils.isEmpty(strasseEdit.getText().toString())
                || TextUtils.isEmpty(plzEdit.getText().toString())
                || TextUtils.isEmpty(hausNrEdit.getText().toString())
                || TextUtils.isEmpty(ortEdit.getText().toString())
        ){
            pruefung = "Es müssen alle Felder gefüllt werden";
        }else{
            String checkPW = passwortEdit.getText().toString();
            String checkWieder = passwortWiederEdit.getText().toString();
            if(checkPW.equals(checkWieder)){
                emailAuth = eMailEdit.getText().toString();
                pwAuth = passwortEdit.getText().toString();
                pruefung = "OK";
            }else{
                pruefung = "Die Passworte stimmen nicht überein";
            }
        }
    }

    private void addFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailAuth, pwAuth).addOnCompleteListener(
                RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Intent intent = new Intent();
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this,"Erfolgreich registriert. Sie können sich anmelden",
                                    Toast.LENGTH_SHORT).show();
                            RegisterActivity.super.onBackPressed();

                        }else{
                            Log.d(TAG, "Fehler");
                            Toast.makeText(RegisterActivity.this,
                                    "Der Nutzer exisitert schon. Bitte melden Sie sich an",
                                    Toast.LENGTH_LONG).show();
                            RegisterActivity.super.onBackPressed();
                        }
                    }
                }
        );
    }

    private void addPlayer() {
        email = eMailEdit.getText().toString();
        vorname = vorNameEdit.getText().toString();
        name = nameEdit.getText().toString();
        strasse = strasseEdit.getText().toString();
        hausnr = hausNrEdit.getText().toString();
        plz = plzEdit.getText().toString();
        ort = ortEdit.getText().toString();

        OkHttpClient client = new OkHttpClient();
        String postPlayerUrl = "https://qu-iu-zz.beyer-its.de/ins_player.php";

        Log.d(TAG, "email " + email);
        RequestBody reqBody = new FormBody.Builder()
                .add("email", email)
                .add("vorname", vorname)
                .add("name", name)
                .add("strasse", strasse)
                .add("hausnr", hausnr)
                .add("plz", plz)
                .add("ort", ort)
                .build();

        Request request = new Request.Builder().url(postPlayerUrl).post(reqBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "Fehler beim Insert" + e.getMessage());
                pruefung = "NOK";
                Toast.makeText(RegisterActivity.this, "Spieler hinzufügen fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
               try {
                   myResp = response.body().string();
                   Log.d(TAG, "response Inhalt: " + myResp);
                   if (response.isSuccessful()){
                     pruefung = "OK";
                   }else {
                       Log.d(TAG, "Fehler" + response);
                       pruefung = "NOK";
                   }
               }catch (IOException e) {
                   Log.d(TAG, "Exception: " + e.getMessage());
               }

            }
        });
    }
}