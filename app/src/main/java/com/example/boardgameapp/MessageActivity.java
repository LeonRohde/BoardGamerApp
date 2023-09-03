package com.example.boardgameapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MessageActivity extends AppCompatActivity {
    private Button btnNotificationSend, btnNotificationAbbruch;
    private final String TAG = "MessageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnNotificationAbbruch = findViewById(R.id.btnNotificationAbbruch);
        btnNotificationSend = findViewById(R.id.btnNotifcationSend);

        btnNotificationAbbruch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MessageActivity.this, "Senden abgebrochen", Toast.LENGTH_SHORT).show();
                MessageActivity.super.onBackPressed();
            }
        });
        btnNotificationSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titelEdit = findViewById(R.id.editTextSendNotificationTitel);
                EditText msgEdit = findViewById(R.id.editTextSendNotificationInhalt);
                String titel, msg;

                if (titelEdit.getText().toString().isEmpty() || msgEdit.getText().toString().isEmpty()){
                    Toast.makeText(MessageActivity.this, "Es müssen alle Felder gefüllt werden", Toast.LENGTH_SHORT).show();
                }else {
                    titel = titelEdit.getText().toString();
                    msg = msgEdit.getText().toString();
                    FirebaseMessaging.getInstance().subscribeToTopic("Information")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msgTopic = "Subscribed";
                                    if (!task.isSuccessful()) {
                                        msgTopic = "Subscribe failed";
                                    }
                                    Log.d(TAG, msgTopic);
                                    Toast.makeText(MessageActivity.this, msgTopic, Toast.LENGTH_SHORT).show();
                                }
                            });

                    sendNotification(titel, msg);

                    Toast.makeText(MessageActivity.this, "Nachricht versendet", Toast.LENGTH_SHORT).show();
                    MessageActivity.super.onBackPressed();
                }

            }
        });
    }

    private void sendNotification(String titel, String msg) {
    }
}
