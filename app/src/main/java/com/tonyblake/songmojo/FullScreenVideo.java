package com.tonyblake.songmojo;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FullScreenVideo extends AppCompatActivity{

    private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;

    private View chronometer_icon, exit_fullscreen_icon;

    private Chronometer chronometer;

    private LayoutInflater inflator;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_video);

        // Get Camera for preview
        myCamera = Utils.getCameraInstance();
        if(myCamera == null){
            Toast.makeText(FullScreenVideo.this,
                    "Fail to get Camera",
                    Toast.LENGTH_LONG).show();
        }

        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        FrameLayout myCameraPreview = (FrameLayout)findViewById(R.id.videoview);
        myCameraPreview.addView(myCameraSurfaceView);

        inflator = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        // Overlay chronometer icon
        chronometer_icon = inflator.inflate(R.layout.chronometer,null);
        myCameraPreview.addView(chronometer_icon);

        // Overlay exit fullscreen icon
        exit_fullscreen_icon = inflator.inflate(R.layout.exit_fullscreen_icon,null);
        myCameraPreview.addView(exit_fullscreen_icon);
    }

    @Override
    protected void onResume(){
        super.onResume();

        exit_fullscreen_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }
}
