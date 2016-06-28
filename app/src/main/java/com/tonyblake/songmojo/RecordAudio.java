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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RecordAudio extends AppCompatActivity implements FileSentDialog.FileSentDialogInterface,
                                                            CueBackingTrackDialog.CueBackingTrackDialogInterface,
                                                            RecordDialog.RecordDialogInterface{

    private FirebaseStorage storage;

    private DBManager dbManager;

    private SimpleDateFormat simpleDateFormat;

    private String currentDateandTime;

    private TimeZone gmtTime;

    private StorageReference storageRef, recordingRef;

    private Context context;

    private String firstName;

    public static String filename, recipient;

    private TextView tv_filename;

    private TextView tv_recipient;

    private Button cue_backing_track, record, play, pause, stop, send;

    private MediaRecorder myAudioRecorder;

    private Toolbar actionBar;

    private String filePath;

    private String backing_track;

    private File file;

    private CueBackingTrackDialog cueBackingTrackDialog;

    private ProgressDialog sendingFileDialog;

    private FragmentManager fm;

    private boolean backing_track_cued;

    private MediaPlayer mediaPlayer;

    private boolean paused;

    private boolean backing_track_playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);

        context = this;

        fm = getSupportFragmentManager();

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

        cue_backing_track = (Button) findViewById(R.id.btn_cue_backing_track);
        cue_backing_track.setBackgroundResource(android.R.drawable.btn_default);

        record = (Button) findViewById(R.id.btn_record);
        record.setBackgroundResource(android.R.drawable.btn_default);

        play = (Button) findViewById(R.id.btn_play);
        play.setBackgroundResource(android.R.drawable.btn_default);

        pause = (Button) findViewById(R.id.btn_pause);
        pause.setBackgroundResource(android.R.drawable.btn_default);

        stop = (Button) findViewById(R.id.btn_stop);
        stop.setBackgroundResource(android.R.drawable.btn_default);

        send = (Button) findViewById(R.id.btn_send);
        send.setBackgroundResource(android.R.drawable.btn_default);

        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);
        send.setEnabled(false);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename + context.getString(R.string._mp3);

        file = new File(filePath);

        backing_track_cued = false;

        myAudioRecorder = new MediaRecorder();

        mediaPlayer = new MediaPlayer();

        paused = false;

        backing_track_playing = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        tv_filename.setText(filename + context.getString(R.string._mp3));

        tv_recipient.setText(recipient);

        cue_backing_track.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (backing_track_cued) {

                    cue_backing_track.setBackgroundResource(android.R.drawable.btn_default);

                    backing_track_cued = false;

                    Toast.makeText(context, context.getString(R.string.backing_track_removed), Toast.LENGTH_SHORT).show();
                } else {

                    cueBackingTrackDialog = new CueBackingTrackDialog();
                    cueBackingTrackDialog.show(fm, "CueBackingTrackDialog");
                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(backing_track_cued){

                    playFile(backing_track);

                    backing_track_playing = true;
                }

                RecordDialog recordDialog = new RecordDialog();

                Bundle bundle = new Bundle();

                bundle.putString("filepath", filePath);

                recordDialog.setArguments(bundle);

                recordDialog.show(fm, "recordDialog");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                Toast.makeText(context,context.getString(R.string.track_playing),Toast.LENGTH_SHORT).show();

                playFile(filePath);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();

                    paused = true;
                }

                record.setEnabled(false);
                stop.setEnabled(false);
                play.setEnabled(true);
                pause.setEnabled(false);
                send.setEnabled(false);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mediaPlayer.stop();

                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                pause.setEnabled(false);
                send.setEnabled(true);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                myAudioRecorder.release();

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

    private void playFile(String filePath){

        if(paused) {

            mediaPlayer.start();

            paused = false;
        }
        else{

            mediaPlayer.reset();

            try {

                mediaPlayer.setDataSource(filePath);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            try {

                mediaPlayer.prepare();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        mediaPlayer.start();

        record.setEnabled(false);
        stop.setEnabled(true);
        play.setEnabled(false);
        pause.setEnabled(true);
        send.setEnabled(false);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                pause.setEnabled(false);
                send.setEnabled(true);
            }
        });
    }

    @Override
    public void onDoneButtonClick(DialogFragment dialog) {

        Intent intent = new Intent(this, Home.class);

        intent.putExtra("firstName", firstName);

        startActivity(intent);
    }

    @Override
    public void onSelectButtonClick(DialogFragment dialog, String fileChosen) {

        cue_backing_track.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.clr_pressed));

        backing_track_cued = true;

        backing_track = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileChosen;

        String background_track_message = fileChosen + " " + context.getString(R.string.set_as_backing_track);

        Toast.makeText(context,background_track_message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopButtonClick(DialogFragment dialog) {

        if(backing_track_playing){

            mediaPlayer.stop();

            record.setEnabled(true);
            stop.setEnabled(false);
            play.setEnabled(true);
            pause.setEnabled(false);
            send.setEnabled(true);

            backing_track_playing = false;

            cue_backing_track.setBackgroundResource(android.R.drawable.btn_default);

            backing_track_cued = false;
        }

        Toast.makeText(context,context.getString(R.string.track_saved),Toast.LENGTH_SHORT).show();

        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(true);
        pause.setEnabled(false);
        send.setEnabled(true);
    }
}
