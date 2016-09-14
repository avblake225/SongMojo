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

    private FileSent fileSent;

    public SendMessageToRecipientTask(FileSent fileSent){

        this.fileSent = fileSent;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        String dateAndTime = Utils.getCurrentDateAndTime();

        String[] senderName = Utils.separateWords(fileSent.sender);

        String senderfirstname = senderName[0];

        String senderlastname = senderName[1];

        String[] recipientName = Utils.separateWords(fileSent.recipient);

        String recipientfirstname = recipientName[0];

        String recipientlastname = recipientName[1];

        String filenameWithoutPrefix = Utils.removePrefix(fileSent.filename);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("DateAndTime", dateAndTime)
                .add("SenderToken", fileSent.token)
                .add("SenderFirstname", senderfirstname)
                .add("SenderLastname", senderlastname)
                .add("RecipientFirstname", recipientfirstname)
                .add("RecipientLastname", recipientlastname)
                .add("FileName", filenameWithoutPrefix)
                .add("FileType", fileSent.filetype)
                .add("Duration", fileSent.duration)
                .build();

        Request request = new Request.Builder()
                .url("http://tonyonandroid.com/songmojo/activity.php")
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
