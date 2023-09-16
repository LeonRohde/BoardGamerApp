package com.example.boardgameapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFCMService extends FirebaseMessagingService {
    private static String TAG = "MyFCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");
        super.onMessageReceived(remoteMessage);
        getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        Intent msgActivity = new Intent(MyFCMService.this, MessageActivity.class);
        String msgTitel = remoteMessage.getNotification().getTitle();
        String msgBody = remoteMessage.getNotification().getBody();
        msgActivity.putExtra("titel", msgTitel);
        msgActivity.putExtra("body", msgBody);
        msgActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(msgActivity);
    }

    @SuppressLint("MissingPermission")
    public void getFirebaseMessage(String title, String msg)
    {
        Log.d(TAG, "getFirebaseMessage");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"myFirebaseChannel")
                .setSmallIcon(R.drawable.baseline_games_24)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(101,builder.build());
    }

}
