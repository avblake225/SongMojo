package com.tonyblake.songmojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String FCMRegistrationToken = FirebaseInstanceId.getInstance().getToken();

        Intent intent = new Intent(this, Login.class);

        startActivity(intent);

        finish();
    }
}