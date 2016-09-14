package com.tonyblake.songmojo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRegistrationTask extends AsyncTask<String,Void,Boolean>{

    private String token, firstName, lastName, fullname, email, password;

    public UserRegistrationTask(String token, String firstName, String lastName, String email, String password){

       this.token = token;
       this.firstName = firstName;
       this.lastName = lastName;

       fullname = firstName + " " + lastName;
       this.email = email;
       this.password = password;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .add("FirstName", firstName)
                .add("LastName", lastName)
                .add("FullName", fullname)
                .add("Email", email)
                .add("Password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://tonyonandroid.com/songmojo/userregistration.php")
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
