package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordAudio extends AppCompatActivity implements FileSentDialog.FileSentDialogInterface,
                                                            CueBackgroundTrackDialog.CueBackgroundTrackDialogInterface{

    private FirebaseStorage storage;

    private StorageReference storageRef, recordingRef;

    private Context context;

    public static String filename, recipient;

    private TextView tv_filename;

    private TextView tv_recipient;

    private Button cue_background_track, play, stop, record, send;

    private MediaRecorder myAudioRecorder;

    private Toolbar actionBar;

    private String filePath;

    private CueBackgroundTrackDialog cueBackgroundTrackDialog;

    private ProgressDialog sendingFileDialog;

    private FragmentManager fm;

    private boolean background_track_cued;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);

        context = this;

        fm = getSupportFragmentManager();

        savedInstanceState = getIntent().getExtras();

        filename = savedInstanceState.getString("filename");
        recipient = savedInstanceState.getString("recipient");

        storage = FirebaseStorage.getInstance();

        // Create a storage reference
        storageRef = storage.getReferenceFromUrl("gs://songmojo.appspot.com");

        // Create a reference to recording.3gp
        recordingRef = storageRef.child(filename);

        tv_filename = (TextView) findViewById(R.id.tv_filename);

        tv_recipient = (TextView) findViewById(R.id.tv_recipient);

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

        cue_background_track = (Button) findViewById(R.id.btn_cue_background_track);
        cue_background_track.setBackgroundResource(android.R.drawable.btn_default);

        play = (Button) findViewById(R.id.btn_play);
        play.setBackgroundResource(android.R.drawable.btn_default);

        stop = (Button) findViewById(R.id.btn_stop);
        stop.setBackgroundResource(android.R.drawable.btn_default);

        record = (Button) findViewById(R.id.btn_record);
        record.setBackgroundResource(android.R.drawable.btn_default);

        send = (Button) findViewById(R.id.btn_send);
        send.setBackgroundResource(android.R.drawable.btn_default);

        stop.setEnabled(false);
        play.setEnabled(false);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;

        background_track_cued = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setOutputFile(filePath);

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        tv_filename.setText(filename + context.getString(R.string._mp3));

        tv_recipient.setText(recipient);

        cue_background_track.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(background_track_cued){

                    cue_background_track.setBackgroundResource(android.R.drawable.btn_default);

                    background_track_cued = false;

                    Toast.makeText(context,context.getString(R.string.background_track_removed),Toast.LENGTH_SHORT).show();
                }
                else{

                    cueBackgroundTrackDialog = new CueBackgroundTrackDialog();
                    cueBackgroundTrackDialog.show(fm,"CueBackgroundTrackDialog");
                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                }
                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder  = null;

                stop.setEnabled(false);
                play.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_SHORT).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {

                MediaPlayer m = new MediaPlayer();

                try {

                    m.setDataSource(filePath);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    m.prepare();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();

                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(myAudioRecorder == null){

                    new SendFileTask(filePath, recordingRef) {

                        @Override
                        protected void onPreExecute(){

                            sendingFileDialog = new ProgressDialog(context);
                            sendingFileDialog.setIndeterminate(true);
                            sendingFileDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            sendingFileDialog.setMessage(context.getString(R.string.sending_file));
                            sendingFileDialog.show();
                        }

                        @Override
                        protected void onPostExecute(String fileStatus){

                            sendingFileDialog.dismiss();

                            FileSentDialog fileSentDialog = new FileSentDialog();
                            fileSentDialog.show(fm, "fileSentDialog");
                        }

                    }.execute();
                }
                else{

                    Toast.makeText(context,context.getString(R.string.nothing_to_send),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDoneButtonClick(DialogFragment dialog) {

        Intent intent = new Intent(this, Home.class);
        intent.putExtra("filename", filename);
        intent.putExtra("recipient", recipient);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        intent.putExtra("date", currentDateandTime);

        startActivity(intent);
    }

    @Override
    public void onSelectButtonClick(DialogFragment dialog, String fileChosen) {

        cue_background_track.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.clr_pressed));

        background_track_cued = true;

        String background_track_message = fileChosen + " " + context.getString(R.string.set_as_background_track);

        Toast.makeText(context,background_track_message,Toast.LENGTH_SHORT).show();
    }
}
