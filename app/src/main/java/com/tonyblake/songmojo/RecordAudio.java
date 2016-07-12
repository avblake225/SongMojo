package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class RecordAudio extends AppCompatActivity implements FileSentDialog.FileSentDialogInterface,
                                                            GetFileDialog.GetFileDialogInterface,
                                                            RecordAudioDialog.RecordDialogInterface{

    private FirebaseStorage storage;

    private DBManager dbManager;

    private SimpleDateFormat simpleDateFormat;

    private String currentDateandTime;

    private TimeZone gmtTime;

    private StorageReference storageRef, recordingRef;

    private Context context;

    private ArrayList<String> availableFilenames;

    private String firstName;

    public static String filename, recipient;

    private TextView tv_filename;

    private TextView tv_recipient;

    private Button cue_backing_track, record, play, pause, stop, send;

    private Toolbar actionBar;

    private String filePath;

    private String backing_track;

    private File file;

    private GetFileDialog getFileDialog;

    private ProgressDialog sendingFileDialog;

    private FragmentManager fm;

    private boolean backing_track_cued;

    private MediaPlayer audioPlayer;

    private boolean paused;

    private boolean backing_track_playing;

    private long start_time, stop_time, time_elapsed;

    private String duration;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);

        context = this;

        fm = getSupportFragmentManager();

        savedInstanceState = getIntent().getExtras();

        availableFilenames = savedInstanceState.getStringArrayList("availableFilenames");
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

        // Create a file object
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

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filename;

        file = new File(filePath);

        backing_track_cued = false;

        audioPlayer = new MediaPlayer();

        paused = false;

        backing_track_playing = false;

        database = FirebaseDatabase.getInstance();

        databaseRef = database.getReference().child("files");
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

        tv_filename.setText(filename);

        tv_recipient.setText(recipient);

        cue_backing_track.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (backing_track_cued) {

                    cue_backing_track.setBackgroundResource(android.R.drawable.btn_default);

                    backing_track_cued = false;

                    Toast.makeText(context, context.getString(R.string.backing_track_removed), Toast.LENGTH_SHORT).show();
                } else {

                    getFileDialog = new GetFileDialog();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("availableFilenames", availableFilenames);
                    getFileDialog.setArguments(bundle);

                    getFileDialog.show(fm, "getFileDialog");
                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                start_time = SystemClock.elapsedRealtime() ;

                if(backing_track_cued){

                    playFile(backing_track);

                    backing_track_playing = true;
                }

                RecordAudioDialog recordAudioDialog = new RecordAudioDialog();

                Bundle bundle = new Bundle();

                bundle.putString("filepath", filePath);

                recordAudioDialog.setArguments(bundle);

                recordAudioDialog.show(fm, "recordDialog");
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

                if (audioPlayer.isPlaying()) {

                    audioPlayer.pause();

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

                audioPlayer.stop();

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

                RecordAudioDialog.audioRecorder.release();

                InputStream stream = null;

                try {

                    stream = new FileInputStream(file);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if(Utils.connectedToNetwork(context)){

                    new SendFileTask(context, stream, recordingRef) {

                        @Override
                        protected void onPreExecute() {

                            sendingFileDialog = new ProgressDialog(context);
                            sendingFileDialog.setIndeterminate(true);
                            sendingFileDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            sendingFileDialog.setMessage(context.getString(R.string.sending_file));
                            sendingFileDialog.show();
                        }

                        @Override
                        protected void onPostExecute(String result) {

                            dbManager.insertData(recipient, filename, duration, context.getString(R.string.audio_file), currentDateandTime);

                            AvailableFile availableFile = new AvailableFile(firstName, removePrefix(filename), recipient);

                            databaseRef.child(removePrefix(filename)).setValue(availableFile);

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

    private String removePrefix(String filename){

        char[] chars = new char[filename.length()-4];

        for(int i=0;i<filename.length();i++){

            char letter = filename.charAt(i);

            String letter_str = String.valueOf(letter);

            if(letter_str.equals(".")){

                break;
            }
            else{

                chars[i] = letter;
            }
        }

        String filenameWithoutPrefix = new String(chars);

        return filenameWithoutPrefix;
    }

    private void playFile(String filePath){

        if(paused) {

            audioPlayer.start();

            paused = false;
        }
        else{

            audioPlayer.reset();

            try {

                audioPlayer.setDataSource(filePath);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            try {

                audioPlayer.prepare();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        audioPlayer.start();

        record.setEnabled(false);
        stop.setEnabled(true);
        play.setEnabled(false);
        pause.setEnabled(true);
        send.setEnabled(false);

        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

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
    public void onGetFileDialogOkButtonClick(DialogFragment dialog, String fileChosen) {

        cue_backing_track.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.clr_pressed));

        backing_track_cued = true;

        backing_track = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SongMojo/Downloads/" + fileChosen + context.getString(R.string._mp3);

        String background_track_message = fileChosen + " " + context.getString(R.string.set_as_backing_track);

        Toast.makeText(context,background_track_message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopButtonClick(DialogFragment dialog) {

        stop_time = SystemClock.elapsedRealtime();

        time_elapsed = stop_time - start_time;

        duration = Utils.formatInterval(time_elapsed);

        if(backing_track_playing){

            audioPlayer.stop();

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
