package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    Button btn_record, btn_stop, btn_play, btn_send;

    boolean recording;

    private int cameraId;

    private Toolbar actionBar;

    private Context context;

    private TextView tv_filename;

    private String filepath;

    private File file;

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

        //Get Camera for preview
        myCamera = getCameraInstance();
        if(myCamera == null){
            Toast.makeText(RecordVideo.this,
                    "Fail to get Camera",
                    Toast.LENGTH_LONG).show();
        }

        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        FrameLayout myCameraPreview = (FrameLayout)findViewById(R.id.videoview);
        myCameraPreview.addView(myCameraSurfaceView);

        btn_record = (Button)findViewById(R.id.btn_record);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_play = (Button)findViewById(R.id.btn_play);
        btn_send = (Button)findViewById(R.id.btn_send);

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;

        file = new File(filepath);
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

        btn_record.setOnClickListener(new View.OnClickListener() {

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

        btn_stop.setOnClickListener(new View.OnClickListener() {

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

                } else {

                    //Release Camera before MediaRecorder start
                    releaseCamera();
                }
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(context,
                        "Video playback currently unavailable",
                        Toast.LENGTH_LONG).show();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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

                            dbManager.insertData(recipient, filename, currentDateandTime);

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
    }

    private Camera getCameraInstance(){
        // TODO Auto-generated method stub
        Camera c = null;
        try {

            cameraId = findFrontFacingCamera();

            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private boolean prepareVideoRecorder(){

        myCamera = getCameraInstance();

        videoRecorder = new MediaRecorder();

        myCamera.unlock();

        videoRecorder.setCamera(myCamera);

        videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        videoRecorder.setProfile(CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH));

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
        releaseCamera();
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