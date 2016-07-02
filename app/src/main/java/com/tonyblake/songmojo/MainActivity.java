package com.tonyblake.songmojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Login.class);
        /*Intent intent = new Intent(this, RecordVideo.class);
        intent.putExtra("firstName","some name");
        intent.putExtra("filename", "some file");
        intent.putExtra("recipient","some recipient");*/

        startActivity(intent);

        finish();
    }
}