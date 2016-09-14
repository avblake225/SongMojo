package com.tonyblake.songmojo;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateTokenTask extends AsyncTask<String,Void,Boolean> {

    private String newToken;
    private String email;
    private String password;

    public UpdateTokenTask(String newToken, String email, String password){

        this.newToken = newToken;
        this.email = email;
        this.password = password;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("NewToken", newToken)
                .add("Email", email)
                .add("Password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://tonyonandroid.com/songmojo/updatetoken.php")
                .post(body)
                .build();

        try {

            Response response = client.newCall(request).execute();

            int responseCode = response.code();

            if(responseCode == 200){

                result = true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
