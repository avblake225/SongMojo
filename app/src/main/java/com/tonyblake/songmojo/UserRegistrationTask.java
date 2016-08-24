package com.tonyblake.songmojo;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRegistrationTask extends AsyncTask<String,Void,Boolean>{

    private String token, fullname;

    public UserRegistrationTask(String token, String fullname){

       this.token = token;
       this.fullname = fullname;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .add("User", fullname)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.10/songmojo/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();

            result = true;

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
