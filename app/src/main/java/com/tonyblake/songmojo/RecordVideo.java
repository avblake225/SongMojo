package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RecordVideo extends AppCompatActivity implements FileSentDialog.FileSentDialogInterface{

    private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;
    private MediaRecorder videoRecorder;

    public static String firstName, filename, recipient;

    private FirebaseStorage storage;

    private DBManager dbManager;

    private SimpleDateFormat simpleDateFormat;

    private String currentDateandTime;

    private TimeZone gmtTime;

    private StorageReference storageRef, recordingRef;

    private ProgressDialog sendingFileDialog;

    private FragmentManager fm;

    Button record, stop, play, send;

    boolean recording;

    private Toolbar actionBar;

    private Context context;

    private TextView tv_filename;

    private String filepath;

    private File file;

    private MediaPlayer videoPlayer;

    private View enter_fullscreen_icon;

    private LayoutInflater inflator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        savedInstanceState = getIntent().getExtras();

        firstName = savedInstanceState.getString("firstName");
        filename = savedInstanceState.getString("filename");
        recipient = savedInstanceState.getString("recipient");

        dbManager = Home.dbManager;

        simpleDateFormat = new SimpleDateFormat();

        gmtTime = TimeZone.getTimeZone("GMT");

        simpleDateFormat.setTimeZone(gmtTime);

        currentDateandTime = simpleDateFormat.format(new Date());

        storage = FirebaseStorage.getInstance();

        // Create a storage reference
        storageRef = storage.getReferenceFromUrl("gs://songmojo.appspot.com");

        // Create a reference to recording.3gp
        recordingRef = storageRef.child(filename);

        fm = getSupportFragmentManager();

        recording = false;

        setContentView(R.layout.record_video);

        tv_filename = (TextView)findViewById(R.id.tv_filename);
        tv_filename.setText(filename);

        // Show Status Bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        // Set up Action Bar
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        actionBar.setTitle(context.getString(R.string.app_name));
        actionBar.setTitleTextColor(context.getResources().getColor(R.color.white));

        record = (Button)findViewById(R.id.btn_record);
        stop = (Button)findViewById(R.id.btn_stop);
        play = (Button)findViewById(R.id.btn_play);
        send = (Button)findViewById(R.id.btn_send);

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;

        file = new File(filepath);

        videoPlayer = new MediaPlayer();
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Get Camera for preview
        myCamera = Utils.getCameraInstance();
        if(myCamera == null){
            Toast.makeText(RecordVideo.this,
                    "Fail to get Camera",
                    Toast.LENGTH_LONG).show();
        }

        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        FrameLayout myCameraPreview = (FrameLayout)findViewById(R.id.videoview);
        myCameraPreview.addView(myCameraSurfaceView);

        inflator = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        // Overlay fullscreen icon
        enter_fullscreen_icon = inflator.inflate(R.layout.enter_fullscreen_icon,null);
        myCameraPreview.addView(enter_fullscreen_icon);

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!prepareVideoRecorder()) {

                    Toast.makeText(RecordVideo.this,
                            "Fail in prepareMediaRecorder()!\n - Ended -",
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                videoRecorder.start();
                recording = true;

                Toast.makeText(context,
                        context.getString(R.string.recording),
                        Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (recording) {
                    // stop recording and release camera
                    videoRecorder.stop();  // stop the recording
                    releaseVideoRecorder(); // release the MediaRecorder object

                    //Exit after saved
                    //finish();

                    Toast.makeText(context,
                            context.getString(R.string.video_saved),
                            Toast.LENGTH_LONG).show();

                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(context, context.getString(R.string.video_playing), Toast.LENGTH_LONG).show();

                playFile(filepath);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                releaseCamera();

                InputStream stream = null;

                try {

                    stream = new FileInputStream(file);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if(Utils.connectedToNetwork(context)){

                    new SendFileTask(stream, recordingRef) {

                        @Override
                        protected void onPreExecute() {

                            sendingFileDialog = new ProgressDialog(context);
                            sendingFileDialog.setIndeterminate(true);
                            sendingFileDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            sendingFileDialog.setMessage(context.getString(R.string.sending_file));
                            sendingFileDialog.show();
                        }

                        @Override
                        protected void onPostExecute(String fileStatus) {

                            if(SendFile.audioFile){

                                dbManager.insertData(recipient, filename, context.getString(R.string.audio_file), currentDateandTime);
                            }
                            else if(SendFile.videoFile){

                                dbManager.insertData(recipient, filename, context.getString(R.string.video_file), currentDateandTime);
                            }

                            sendingFileDialog.dismiss();

                            FileSentDialog fileSentDialog = new FileSentDialog();
                            fileSentDialog.show(fm, "fileSentDialog");
                        }

                    }.execute();
                }
                else{

                    Utils.showToastMessage(context, context.getString(R.string.no_network_connection));
                }

            }
        });

        enter_fullscreen_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FullScreenVideo.class);

                startActivity(intent);
            }
        });
    }

    private void playFile(String filePath){


        try {

            videoPlayer.setDataSource(filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {

            videoPlayer.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        videoPlayer.start();

        record.setEnabled(false);
        stop.setEnabled(true);
        play.setEnabled(false);
        send.setEnabled(false);

        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                send.setEnabled(true);
            }
        });
    }

    private boolean prepareVideoRecorder(){

        videoRecorder = new MediaRecorder();

        myCamera.unlock();

        videoRecorder.setCamera(myCamera);

        videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        videoRecorder.setProfile(CamcorderProfile.get(Utils.cameraId, CamcorderProfile.QUALITY_HIGH));

        videoRecorder.setOutputFile(filepath);

        //videoRecorder.setMaxDuration(60000); // Set max duration 60 sec.
        //videoRecorder.setMaxFileSize(5000000); // Set max file size 5M

        videoRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());

        try {

            videoRecorder.prepare();
        }
        catch (IllegalStateException e) {

            releaseVideoRecorder();

            return false;
        }
        catch (IOException e) {

            releaseVideoRecorder();

            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseVideoRecorder();
    }

    private void releaseVideoRecorder(){

        if (videoRecorder != null) {

            videoRecorder.reset();   // clear recorder configuration
            videoRecorder.release(); // release the recorder object
            videoRecorder = null;
            myCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }

    @Override
    public void onDoneButtonClick(DialogFragment dialog) {

        Intent intent = new Intent(this, Home.class);

        intent.putExtra("firstName", firstName);

        startActivity(intent);
    }

    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

        private SurfaceHolder mHolder;
        private Camera mCamera;

        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight,
                                   int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub

        }
    }
}