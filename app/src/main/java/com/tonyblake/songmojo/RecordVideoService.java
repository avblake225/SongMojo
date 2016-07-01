package com.tonyblake.songmojo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;

public class RecordVideoService extends Service {

    private MediaRecorder videoRecorder;

    private Camera camera;

    private MyCameraSurfaceView cameraSurfaceView;

    private String filepath;

    @Override
    public void onCreate() {

        prepareVideoRecorder();

        camera = RecordVideo.myCamera;

        cameraSurfaceView = RecordVideo.myCameraSurfaceView;

        filepath = RecordVideo.filepath;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        videoRecorder.start();

        Toast.makeText(this,"recording service started",Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        videoRecorder.stop();

        Toast.makeText(this,"recording service stopped",Toast.LENGTH_SHORT).show();
    }

    private void prepareVideoRecorder(){

        videoRecorder = new MediaRecorder();

        camera.unlock();

        videoRecorder.setCamera(camera);

        videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        videoRecorder.setProfile(CamcorderProfile.get(Utils.cameraId, CamcorderProfile.QUALITY_HIGH));

        videoRecorder.setOutputFile(filepath);

        //videoRecorder.setMaxDuration(60000); // Set max duration 60 sec.
        //videoRecorder.setMaxFileSize(5000000); // Set max file size 5M

        videoRecorder.setPreviewDisplay(cameraSurfaceView.getHolder().getSurface());

        try {

            videoRecorder.prepare();
        }
        catch (IllegalStateException e) {

        }
        catch (IOException e) {

        }
    }
}
