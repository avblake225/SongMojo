package com.tonyblake.songmojo;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetUserTask extends AsyncTask<String,Void,String>{

    private String email, password;

    public GetUserTask(String email, String password){

        this.email = email;
        this.password = password;
    }
    @Override
    protected String doInBackground(String... params) {

        String user = "";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Email", email)
                .add("Password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.1/songmojo/getuser.php")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            int responseCode = response.code();

            user = response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }
}
