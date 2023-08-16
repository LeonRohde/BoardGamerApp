package com.example.boardgameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    private Button btnAbbruch, btnRegist;
    private String pruefung = new String("");
    private String emailAuth, pwAuth;
    private FirebaseAuth mAuth;
    private EditText eMail, passwort, passwortWieder, vorName, Name, strasse, hausNr, plz, ort;
    private TextView ergebnis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnAbbruch = findViewById(R.id.btnAbbruch);
        btnRegist = findViewById(R.id.btnRegist);

        btnAbbruch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "Abbruch");
                setResult(0, intent);
                RegisterActivity.super.onBackPressed();
            }
        });
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pruefenEingabe();

                if (pruefung.equals("OK")){
                    Log.d("Register", "Prüfung erfolgreich");
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

                                        intent.putExtra("result", "Erfolgreich");
                                        setResult(0, intent);
                                        RegisterActivity.super.onBackPressed();

                                    }else{
                                        Log.d("Register", "Fehler");
                                        Toast.makeText(RegisterActivity.this,
                                                "Der Nutzer exisitert schon. Bitte melden Sie sich an",
                                                Toast.LENGTH_LONG).show();
                                        intent.putExtra("result", "Fehler");
                                        setResult(1, intent);
                                        RegisterActivity.super.onBackPressed();
                                    }
                                }
                            }
                    );
                }else{
                    ergebnis.setText(pruefung);
                }
            }

            private void pruefenEingabe() {
                eMail = (EditText) findViewById(R.id.editTextRegEmail);
                passwort = (EditText) findViewById(R.id.editTextRegPW);
                passwortWieder = (EditText) findViewById(R.id.editTextRegPWWieder);
                vorName = (EditText) findViewById(R.id.editTextRegVorName);
                Name = (EditText) findViewById(R.id.editTextRegName);
                strasse = (EditText) findViewById(R.id.editTextRegStr);
                hausNr = (EditText) findViewById(R.id.editTextRegHausNr);
                plz = (EditText) findViewById(R.id.editTextRegPLZ);
                ort = (EditText) findViewById(R.id.editTextRegOrt);
                ergebnis = (TextView) findViewById(R.id.textPruefung);

                if(TextUtils.isEmpty(eMail.getText().toString())
                || TextUtils.isEmpty(passwort.getText().toString())
                || TextUtils.isEmpty(passwortWieder.getText().toString())
                || TextUtils.isEmpty(vorName.getText().toString())
                || TextUtils.isEmpty(Name.getText().toString())
                || TextUtils.isEmpty(strasse.getText().toString())
                || TextUtils.isEmpty(plz.getText().toString())
                || TextUtils.isEmpty(hausNr.getText().toString())
                || TextUtils.isEmpty(ort.getText().toString())
                ){
                    pruefung = "Es müssen alle Felder gefüllt werden";
                }else{
                    pruefung = "OK";
                }
                if (pruefung.equals("OK")) {
                    String checkPW = passwort.getText().toString();
                    String checkWieder = passwortWieder.getText().toString();
                    if(checkPW.equals(checkWieder)){
                        pruefung = "OK";
                        emailAuth = eMail.getText().toString();
                        pwAuth = passwort.getText().toString();
                    }else{
                        pruefung = "Die Passworte stimmen nicht überein";
                    }
                }
            }
        });
    }
}