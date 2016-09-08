package com.tonyblake.songmojo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String message = remoteMessage.getData().get("message");

        sendNotification(message);

        // TODO: Extract all file data and add to Files Received Table

        // TODO: Download physical file using GetFileTask

        // TODO: Add action/activity to Recent Activity table
        // e.g. String action = "Received some file from someone";

        String filename = Utils.extractFilename(message);

        NewFileReceivedManager manager = new NewFileReceivedManager(this);

        boolean addedToDatabase = manager.addToDatabase(filename);

        Log.i("Filename from message: ", filename);
    }

    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_mail_outline_black_24dp)
                .setContentTitle("SongMojo")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}