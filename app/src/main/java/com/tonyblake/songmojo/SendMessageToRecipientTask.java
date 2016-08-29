package com.tonyblake.songmojo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessageToRecipientTask extends AsyncTask<String,Void,Boolean> {

    private String token;
    private String sender;
    private String recipient;
    private String filename;

    public SendMessageToRecipientTask(String token, String sender, String recipient, String filename){

        this.token = token;
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        String dateAndTime = Utils.getCurrentDateAndTime();

        String[] senderName = Utils.separateWords(sender);

        String senderfirstname = senderName[0];

        String senderlastname = senderName[1];

        String[] recipientName = Utils.separateWords(recipient);

        String recipientfirstname = recipientName[0];

        String recipientlastname = recipientName[1];

        String filenameWithoutPrefix = Utils.removePrefix(filename);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("DateAndTime", dateAndTime)
                .add("SenderToken", token)
                .add("SenderFirstname", senderfirstname)
                .add("SenderLastname", senderlastname)
                .add("RecipientFirstname", recipientfirstname)
                .add("RecipientLastname", recipientlastname)
                .add("Filename", filenameWithoutPrefix)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.10/songmojo/activitymonitor.php")
                .post(body)
                .build();

        try {

            Response response = client.newCall(request).execute();

            Log.i("Server Response: ", response.body().string());

            result = true;

            Log.i("SendMessageToRecipientT","Successfully sent message to recipient");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
