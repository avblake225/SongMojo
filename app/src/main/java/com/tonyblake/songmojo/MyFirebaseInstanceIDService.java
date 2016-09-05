package com.tonyblake.songmojo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        NewTokenManager manager = new NewTokenManager(this);

        boolean addedToDatabase = manager.addToDatabase(token);

        Log.i("onTokenRefresh: ", "New device token stored in local DB");
    }

    private void sendRegistrationToServer(String token) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder().add("Token", token).build();

        Request request = new Request.Builder()
                            .url("http://192.168.1.3/fcm/register.php")
                            .post(body)
                            .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}