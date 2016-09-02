package com.tonyblake.songmojo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRegistrationTask extends AsyncTask<String,Void,Boolean>{

    private String token, firstName, lastName;

    public UserRegistrationTask(String token, String firstName, String lastName){

       this.token = token;
       this.firstName = firstName;
       this.lastName = lastName;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .add("FirstName", firstName)
                .add("LastName", lastName)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.2/songmojo/release/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();

            result = true;

            Log.i("UserRegistrationTask","Successfully posted user registration data to server");

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
