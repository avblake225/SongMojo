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
import android.util.Log;
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

public class RecordAudio extends AppCompatActivity implements EditFilenameDialog.EditFilenameDialogInterface,
                                                            EditRecipientDialog.EditRecipientDialogInterface,
                                                            FileSentDialog.FileSentDialogInterface,
                                                            CueBackingTrackDialog.CueBackingTrackDialogInterface,
                                                            RecordAudioDialog.RecordDialogInterface{

    private FirebaseStorage storage;

    private DBManager dbManager;

    private StorageReference storageRef, recordingRef;

    private Context context;

    private String user;

    public static String filename, recipient;

    private TextView tv_filename;

    private TextView tv_recipient;

    private Button cue_backing_track, record, play, send;

    private Toolbar actionBar;

    private String filePath;

    private String backing_track;

    private File file;

    private CueBackingTrackDialog cueBackingTrackDialog;

    private ProgressDialog sendingFileDialog;

    private FragmentManager fm;

    private boolean backing_track_cued;

    private boolean backing_track_playing;

    private long start_time, stop_time, duration;

    private String duration_str;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    private TextView btn_edit_filename, btn_edit_recipient;

    private MediaPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);

        context = this;

        audioPlayer = new MediaPlayer();

        fm = getSupportFragmentManager();

        savedInstanceState = getIntent().getExtras();

        user = savedInstanceState.getString("user");
        filename = savedInstanceState.getString("filename");
        recipient = savedInstanceState.getString("recipient");

        dbManager = Home.dbManager;

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
        play.setEnabled(false);

        send = (Button) findViewById(R.id.btn_send);
        send.setBackgroundResource(android.R.drawable.btn_default);
        send.setEnabled(false);

        filePath = Home.recordingsDirectory + File.separator + filename;

        file = new File(filePath);

        backing_track_cued = false;

        backing_track_playing = false;

        database = FirebaseDatabase.getInstance();

        databaseRef = database.getReference().child("files");

        btn_edit_filename = (TextView)findViewById(R.id.btn_edit_filename);

        btn_edit_recipient = (TextView)findViewById(R.id.btn_edit_recipient);
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

        btn_edit_filename.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditFilenameDialog editFilenameDialog = new EditFilenameDialog();
                editFilenameDialog.show(fm, "editFilenameDialogInterface");

            }
        });

        tv_recipient.setText(recipient);

        btn_edit_recipient.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditRecipientDialog editRecipientDialog = new EditRecipientDialog();

                Bundle bundle = new Bundle();

                bundle.putStringArrayList("availableRecipients", Utils.getBandMembers(context, user, dbManager));

                editRecipientDialog.setArguments(bundle);

                editRecipientDialog.show(fm, "editRecipientDialog");
            }
        });

        cue_backing_track.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (backing_track_cued) {

                    cue_backing_track.setBackgroundResource(android.R.drawable.btn_default);

                    backing_track_cued = false;

                    Toast.makeText(context, context.getString(R.string.backing_track_removed), Toast.LENGTH_SHORT).show();
                } else {

                    cueBackingTrackDialog = new CueBackingTrackDialog();

                    Bundle bundle = new Bundle();

                    bundle.putString("user", user);

                    cueBackingTrackDialog.setArguments(bundle);

                    cueBackingTrackDialog.show(fm, "getFileDialog");
                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                start_time = SystemClock.elapsedRealtime();

                if(backing_track_cued){

                    playFile(backing_track);

                    backing_track_playing = true;
                }

                RecordAudioDialog recordAudioDialog = new RecordAudioDialog();

                Bundle bundle = new Bundle();

                bundle.putString("filepath", filePath);

                recordAudioDialog.setArguments(bundle);

                recordAudioDialog.show(fm, "recordAudioDialog");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                start_time = SystemClock.elapsedRealtime();

                PlayAudioDialog playAudioDialog = new PlayAudioDialog();

                Bundle bundle = new Bundle();

                bundle.putString("filename", filename);

                bundle.putString("filepath", filePath);

                playAudioDialog.setArguments(bundle);

                playAudioDialog.show(fm, "playAudioDialog");
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

                    new SendMessageToRecipientTask(Utils.getDeviceToken(), user, recipient, filename) {

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

                                dbManager.insertDataIntoFilesSentTable(user, recipient, filename, duration_str, context.getString(R.string.audio_file), Utils.getCurrentDateAndTime()); // save locally

                                String current_date = Utils.getCurrentDate();

                                String current_time = Utils.getCurrentTime();

                                String action = filename + " sent to " + recipient;

                                dbManager.insertDataIntoRecentActivityTable(user, current_date, current_time, action);

                                AvailableFile availableFile = new AvailableFile(user, Utils.removePrefix(filename), recipient, Utils.getCurrentDateAndTime(), duration_str, context.getString(R.string.audio_file));

                                databaseRef.child(Utils.removePrefix(filename)).setValue(availableFile); // upload to remote DB

                                sendingFileDialog.dismiss();

                                FileSentDialog fileSentDialog = new FileSentDialog();

                                Bundle bundle = new Bundle();

                                bundle.putString("user", user);

                                fileSentDialog.setArguments(bundle);

                                fileSentDialog.show(fm, "fileSentDialog");
                            }
                            else{

                                Toast.makeText(context, context.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }.execute();
                }
                else{

                    Toast.makeText(context, context.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playFile(String filePath){

        try {

            audioPlayer.setDataSource(filePath);
            audioPlayer.prepare();
            audioPlayer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDoneButtonClick(DialogFragment dialog, String user) {

        Intent intent = new Intent(this, Home.class);

        intent.putExtra("user", user);

        startActivity(intent);
    }

    @Override
    public void onCueBackingTrackDialogOkButtonClick(DialogFragment dialog, String fileChosen) {

        cue_backing_track.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.clr_pressed));

        backing_track_cued = true;

        backing_track = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SongMojo/Downloads/" + fileChosen + context.getString(R.string._mp3);

        String background_track_message = fileChosen + " " + context.getString(R.string.set_as_backing_track);

        Toast.makeText(context,background_track_message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordDialogStopButtonClick(DialogFragment dialog) {

        stop_time = SystemClock.elapsedRealtime();

        duration = stop_time - start_time;

        duration_str = Utils.formatInterval(duration);

        if(backing_track_playing){

            audioPlayer.stop();
            audioPlayer.reset();

            backing_track_playing = false;

            cue_backing_track.setBackgroundResource(android.R.drawable.btn_default);

            backing_track_cued = false;
        }

        String msg = filename + " " + context.getString(R.string.saved_to_device);

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        play.setEnabled(true);
        send.setEnabled(true);
    }

    @Override
    public void onEditFilenameDialogOkButtonClick(DialogFragment dialog, String new_filename) {

        filename = new_filename + context.getString(R.string._mp3);

        tv_filename.setText(filename);

        Toast.makeText(context, context.getString(R.string.filename_updated), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditRecipientDialogOkButtonClick(DialogFragment dialog, String new_recipient) {

        recipient = new_recipient;

        tv_recipient.setText(recipient);

        Toast.makeText(context, context.getString(R.string.recipient_updated), Toast.LENGTH_SHORT).show();
    }
}
