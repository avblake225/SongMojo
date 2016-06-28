package com.tonyblake.songmojo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class FullScreenVideo extends AppCompatActivity{

    private ImageView fullscreen_exit_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_video);

        fullscreen_exit_icon = (ImageView)findViewById(R.id.fullscreen_icon);
    }

    @Override
    protected void onResume(){
        super.onResume();

        fullscreen_exit_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }
}
