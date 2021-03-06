package com.tonyblake.songmojo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.SystemClock;

import java.io.IOException;

public class RecordVideoService extends Service {

    public static MediaRecorder videoRecorder;

    private Camera camera;

    private MyCameraSurfaceView cameraSurfaceView;

    private String filepath;

    public static long time_elapsed;

    public static boolean recording;

    @Override
    public void onCreate() {

        camera = RecordVideo.myCamera;

        cameraSurfaceView = RecordVideo.myCameraSurfaceView;

        filepath = RecordVideo.filepath;

        time_elapsed = 0;

        prepareVideoRecorder();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        videoRecorder.start();

        recording = true;

        time_elapsed = SystemClock.elapsedRealtime();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        videoRecorder.stop();
    }

    private void prepareVideoRecorder(){

        videoRecorder = new MediaRecorder();

        camera.unlock();

        videoRecorder.setCamera(camera);

        videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        videoRecorder.setProfile(CamcorderProfile.get(Utils.cameraId, CamcorderProfile.QUALITY_HIGH));

        videoRecorder.setOutputFile(filepath);

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
