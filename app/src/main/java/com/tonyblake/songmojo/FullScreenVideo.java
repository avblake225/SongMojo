package com.tonyblake.songmojo;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FullScreenVideo extends AppCompatActivity{

    private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;
    private MediaRecorder videoRecorder;

    private Context context;

    private View fullscreen_exit_icon;

    //private Chronometer chronometer;

    private LayoutInflater inflator;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_video);

        context = this;

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

        // Overlay fullscreen icon
        fullscreen_exit_icon = inflator.inflate(R.layout.exit_fullscreen_icon,null);
        myCameraPreview.addView(fullscreen_exit_icon);
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
