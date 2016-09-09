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

import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        context = this;

        String message = remoteMessage.getData().get("message");

        ArrayList<String> file_info = getFileInfo(message);

        String dateAndTime = file_info.get(0);
        String sender = file_info.get(1);
        String filename = file_info.get(2);
        String filetype = file_info.get(3);
        String duration = file_info.get(4);

        FileReceived fileReceived = new FileReceived(dateAndTime,sender,filename,filetype,duration);

        NewFileReceivedManager newFileReceivedManager = new NewFileReceivedManager(this);

        if(newFileReceivedManager.addToDatabase(fileReceived)){

            Log.i("OnMessageReceived: ", "Added data to Files Received table");
        }
        else{

            Log.i("OnMessageReceived: ", "Error adding data to Files Received table");
        }

        if(newFileReceivedManager.addStatus(filename, context.getString(R.string.available))){

            Log.i("OnMessageReceived: ", "Added status to File Available table");
        }
        else{

            Log.i("OnMessageReceived: ", "Error adding status to File Available table");
        }

        // TODO: Add action/activity to Recent Activity table
        // e.g. String action = "Received some file from someone";

        sendNotification(filename,sender);
    }

    private void sendNotification(String filename, String sender) {

        String message = sender + " just sent you " + filename;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_mail_outline_black_24dp)
                .setContentTitle("SongMojo")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private ArrayList<String> getFileInfo(String message){

        ArrayList<String> file_info = new ArrayList();

        for(int i=0;i<message.length();i++){

            String message_char = String.valueOf(message.charAt(i));

            if(message_char.equals(",")){

                String info = message.substring(0,i);

                file_info.add(info);

                message = message.substring(i+1,message.length());

                i=0;

                if(file_info.size() == 4){

                    info = message.substring(i,message.length());

                    file_info.add(info);

                    break;
                }
            }
        }

        return file_info;
    }
}