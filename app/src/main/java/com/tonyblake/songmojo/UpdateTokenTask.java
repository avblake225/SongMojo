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
    private String firstName;
    private String lastName;

    public UpdateTokenTask(String newToken, String firstName, String lastName){

        this.newToken = newToken;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("NewToken", newToken)
                .add("FirstName", firstName)
                .add("LastName", lastName)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.1/songmojo/updatetoken.php") // TODO: Change URL on server deployment
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
