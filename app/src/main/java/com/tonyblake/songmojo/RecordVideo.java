package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RecordVideo extends AppCompatActivity implements FileSentDialog.FileSentDialogInterface{

    public static Camera myCamera;

    public static MyCameraSurfaceView myCameraSurfaceView;

    private String user;

    public static String filename, recipient;

    private FirebaseStorage storage;

    private DBManager dbManager;

    private SimpleDateFormat simpleDateFormat;

    private String currentDateandTime;

    private TimeZone gmtTime;

    private StorageReference storageRef, recordingRef;

    private ProgressDialog sendingFileDialog;

    private FragmentManager fm;

    Button record, stop, play, send;

    private Toolbar actionBar;

    private Context context;

    private TextView tv_filename;

    public static String filepath;

    private File file;

    private View stopwatch_icon, enter_fullscreen_icon;

    private Chronometer stopwatch;

    private LayoutInflater inflator;

    private Intent recordVideoIntent;

    private DisplayMetrics displayMetrics;

    private Display display;

    private int display_height;

    private long start_time, stop_time, time_elapsed;

    private String duration;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_video);

        context = this;

        savedInstanceState = getIntent().getExtras();

        user = savedInstanceState.getString("user");
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
        record.setEnabled(true);

        stop = (Button)findViewById(R.id.btn_stop);
        stop.setEnabled(false);

        play = (Button)findViewById(R.id.btn_play);
        play.setEnabled(false);

        send = (Button)findViewById(R.id.btn_send);
        send.setEnabled(false);

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;

        file = new File(filepath);

        displayMetrics = this.getResources().getDisplayMetrics();

        // Get display height
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        display_height = size.y; // px

        //makeVideoViewHeightDynamic(); // Needs debugging

        database = FirebaseDatabase.getInstance();

        databaseRef = database.getReference().child("files");
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Get Camera for preview
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

        // Overlay chronometer icon
        stopwatch_icon = inflator.inflate(R.layout.chronometer,null);
        myCameraPreview.addView(stopwatch_icon);
        stopwatch = (Chronometer)stopwatch_icon.findViewById(R.id.stopwatch);

        if(RecordVideoService.recording){

            stopwatch.setBase(RecordVideoService.time_elapsed);
            stopwatch.start();
        }

        // Overlay enter fullscreen icon
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

                record.setEnabled(false);
                play.setEnabled(false);
                stop.setEnabled(true);
                send.setEnabled(false);

                recordVideoIntent = new Intent(context, RecordVideoService.class);
                startService(recordVideoIntent);

                start_time = SystemClock.elapsedRealtime();
                stopwatch.setBase(start_time);
                stopwatch.start();

                RecordVideoService.recording = true;

                Toast.makeText(context,
                        context.getString(R.string.recording),
                        Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                record.setEnabled(true);
                play.setEnabled(true);
                stop.setEnabled(false);
                send.setEnabled(true);

                if (RecordVideoService.recording) {
                    // stop recording and release camera
                    stopService(recordVideoIntent);
                    releaseVideoRecorder(); // release the MediaRecorder object
                    stopwatch.stop();
                    stop_time = SystemClock.elapsedRealtime();
                    RecordVideoService.recording = false;

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

                Intent intent = new Intent(context,VideoPlayback.class);
                intent.putExtra("filepath",filepath);
                startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                time_elapsed = stop_time - start_time;

                duration = Utils.formatInterval(time_elapsed);

                releaseCamera();

                InputStream stream = null;

                try {

                    stream = new FileInputStream(file);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if(Utils.connectedToNetwork(context)){

                    new StoreFileInCloudTask(context, stream, recordingRef){

                        @Override
                        protected void onPostExecute(Boolean success){

                            if(success){

                                Log.i("StoreFileInCloudTask", "Successfully stored file in cloud");
                            }
                            else{

                                Log.i("StoreFileInCloudTask", "Error storing file in cloud");
                            }
                        }
                    }.execute();

                    new SendMessageToRecipientTask(Utils.getDeviceToken(), recipient, filename) {

                        @Override
                        protected void onPreExecute() {

                            sendingFileDialog = new ProgressDialog(context);
                            sendingFileDialog.setIndeterminate(true);
                            sendingFileDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            sendingFileDialog.setMessage(context.getString(R.string.sending_file));
                            sendingFileDialog.show();
                        }

                        @Override
                        protected void onPostExecute(Boolean success) {

                            if(success){

                                dbManager.insertDataIntoFilesSentTable(user, recipient, filename, duration, context.getString(R.string.video_file), currentDateandTime);

                                AvailableFile availableFile = new AvailableFile(user, Utils.removePrefix(filename), recipient, currentDateandTime, duration, context.getString(R.string.audio_file));

                                databaseRef.child(Utils.removePrefix(filename)).setValue(availableFile); // upload to remote DB

                                sendingFileDialog.dismiss();

                                FileSentDialog fileSentDialog = new FileSentDialog();
                                fileSentDialog.show(fm, "fileSentDialog");
                            }
                            else{

                                Toast.makeText(context, context.getString(R.string.database_error),Toast.LENGTH_SHORT).show();
                            }
                        }

                    }.execute();
                }
                else{

                    Toast.makeText(context, context.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
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

    private void makeVideoViewHeightDynamic(){

        // Measure height of app bar
        actionBar.measure(0,0);
        int actionBar_height = actionBar.getMeasuredHeight();

        // Measure tv_filename height
        tv_filename.measure(0,0);
        int tv_filename_height = tv_filename.getMeasuredHeight();

        // Measure height of buttons panel
        LinearLayout buttons_panel = (LinearLayout)findViewById(R.id.buttons_panel);
        buttons_panel.measure(0,0);
        int buttons_panel_height = buttons_panel.getMeasuredHeight();

        // Subtract all from height of videoview layout
        LinearLayout videoview_layout = (LinearLayout)findViewById(R.id.videoview_layout);
        int sum = actionBar_height + tv_filename_height + buttons_panel_height;
        int new_videoview_layout_height_px = display_height - sum;
        int new_videoview_layout_height_dp = pxToDp(new_videoview_layout_height_px);
        videoview_layout.getLayoutParams().height = new_videoview_layout_height_px;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }

    private void releaseVideoRecorder(){

        if (RecordVideoService.videoRecorder != null) {

            RecordVideoService.videoRecorder.reset();   // clear recorder configuration
            RecordVideoService.videoRecorder.release(); // release the recorder object
            RecordVideoService.videoRecorder = null;
            myCamera.lock();           // lock camera for later use
        }
    }

    @Override
    public void onDoneButtonClick(DialogFragment dialog, String user) {

        Intent intent = new Intent(this, Home.class);

        intent.putExtra("user", user);

        startActivity(intent);
    }

    private int pxToDp(int px){

        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}