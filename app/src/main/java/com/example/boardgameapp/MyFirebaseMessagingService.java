package com.example.boardgameapp;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    @SuppressLint("MissingPermission")
    public void getFirebaseMessage(String title, String msg)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"myFirebaseChannel")
                .setSmallIcon(R.drawable.baseline_games_24)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(101,builder.build());
    }

}
