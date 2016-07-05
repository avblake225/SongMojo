package com.tonyblake.songmojo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class VideoPlayback extends AppCompatActivity implements android.view.SurfaceHolder.Callback{

    private SurfaceView surfaceView;

    private MediaPlayer videoPlayer;

    private String filepath;

    private Context context;

    private Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_playback);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

        videoPlayer = null;

        savedInstanceState = getIntent().getExtras();

        filepath = savedInstanceState.getString("filepath");

        context = this;

        // Set up Action Bar
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        actionBar.setTitle(context.getString(R.string.app_name));
        actionBar.setTitleTextColor(context.getResources().getColor(R.color.white));
    }

    @Override
    protected void onResume(){
        super.onResume();

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        try{

            if (videoPlayer != null) {

                videoPlayer.reset();
                videoPlayer.release();

            }
            else{

                getWindow().setFormat(PixelFormat.UNKNOWN);
                videoPlayer = new MediaPlayer();
                videoPlayer.setDataSource(filepath);
                videoPlayer.prepare();

                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                videoPlayer.start();}
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        videoPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
