package com.example.boardgameapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private final String TAG = "MessageActivity";
    String leer = "";
    private Button btnNotificationSend, btnNotificationAbbruch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent receiveIntent = getIntent();
        Log.d(TAG, "Intent erhalten");
        if (receiveIntent != null && receiveIntent.hasExtra("titel")){
            String msgTitel = receiveIntent.getStringExtra("titel");
            String msgBody = receiveIntent.getStringExtra("body");

            EditText editTextTitel = (EditText) findViewById(R.id.editTextSendNotificationTitel);
            editTextTitel.setText(msgTitel);

            EditText editTextBody = (EditText) findViewById(R.id.editTextSendNotificationInhalt);
            editTextBody.setText(msgBody);
            Log.d(TAG, "Felder müssten gefüllt sein");
            editTextBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextBody.clearComposingText();
                }
            });
        }

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
                    FirebaseMessaging.getInstance().subscribeToTopic("BoardGamer")
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
        Log.d(TAG, "Nachricht soll gesendet werden");
        final String postUrl = "https://fcm.googleapis.com/fcm/send";
        final String fcmServerKey = "AAAAvL5sqkc:APA91bGBHRP2zPEc7eShRi6mZaxsU7QfrZ6ZUvuhGJPrf1y4G6MrJSbe-Sy3pAWxeLPHvDoc1c0r9uO3lp8f8AdTtQhN9w0FM3amptfka2HCDAG5YbUXmYbWjr38YRsWU-WQMYCoCvKm";

        RequestQueue requestQueue;

        requestQueue = Volley.newRequestQueue(MessageActivity.this);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", "/topics/BoardGamer");
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", titel);
            notiObject.put("body", msg);
            notiObject.put("icon", R.drawable.baseline_games_24);

            mainObj.put("notification", notiObject);

            Log.d(TAG, "Request wird erstellt");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj,
                    new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;


                }
            };
            Log.d(TAG, "Request wird ausgeführt");
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
