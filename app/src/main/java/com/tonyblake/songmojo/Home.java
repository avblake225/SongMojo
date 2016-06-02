package com.tonyblake.songmojo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Home extends AppCompatActivity implements DownloadAudioDialog.DownloadAudioDialogListener{

    private Context context;

    private FirebaseStorage storage;

    public static StorageReference storageRef, recordingRef, audioRecordingRef;

    public Home home;

    private Toolbar actionBar;

    private DrawerLayout dLayout;
    private ListView dList;
    private ArrayAdapter<String> drawerAdapter;

    private Intent intent;

    private DownloadAudioDialog downloadAudioDialog;

    private LinearLayout layout_container;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        layout_container = (LinearLayout)findViewById(R.id.layout_container);

        storage = FirebaseStorage.getInstance();

        // Create a storage reference
        storageRef = storage.getReferenceFromUrl("gs://songmojo.appspot.com");

        // Create a reference to recording.3gp
        recordingRef = storageRef.child("recording.3gp");

        // Create a reference to 'audio/recording.3gp'
        audioRecordingRef = storageRef.child("audio/recording.3gp");

        // While the file names are the same, the references point to different files
        recordingRef.getName().equals(audioRecordingRef.getName());    // true
        recordingRef.getPath().equals(audioRecordingRef.getPath());    // false

        // Show Status Bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        context = this;

        home = this;

        // Set up Action Bar
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_menu_white_24dp));
        actionBar.setTitle(context.getString(R.string.app_name));
        actionBar.setTitleTextColor(context.getResources().getColor(R.color.white));
        actionBar.setOverflowIcon(context.getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));

        // Set up Navigation Drawer
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        drawerAdapter = new ArrayAdapter<>(this,R.layout.drawer_item_layout,context.getResources().getStringArray(R.array.menu_items));
        dList.setAdapter(drawerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.overflow_menu, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dLayout.openDrawer(dList);
            }
        });

        dList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fm = getSupportFragmentManager();

                switch (position) {

                    // Upload Audio
                    case 0:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(home, UploadAudio.class);
                        startActivity(intent);

                        break;

                    // Download Audio
                    case 1:

                        dLayout.closeDrawer(dList);

                        downloadAudioDialog = new DownloadAudioDialog();
                        downloadAudioDialog.show(fm, "downloadAudioDialog");

                        break;

                    // Find Band Members
                    case 2:

                        dLayout.closeDrawer(dList);

                        // find band members...

                        break;

                    // Edit Band Members
                    case 3:

                        dLayout.closeDrawer(dList);

                        // edit band members...

                        break;
                }
            }
        });
    }

    @Override
    public void onDownloadAudioDialogSearchClick(DialogFragment dialog, final String filename) {

        StorageReference recordingRef = storageRef.child(filename + ".3gp");

        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);

        recordingRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                displayAudioScreen(filename);
            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(context, context.getString(R.string.no_file_found), Toast.LENGTH_LONG);
            }
        });
    }

    private void displayAudioScreen(String filename){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View download_audio_layout = inflater.inflate(R.layout.download_audio, null);

        layout_container.addView(download_audio_layout);

        TextView tv_file_name = (TextView) download_audio_layout.findViewById(R.id.tv_file_name);

        Button btn_play = (Button) download_audio_layout.findViewById(R.id.btn_play);

        tv_file_name.setText(filename + ".3pg");

        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MediaPlayer mediaPlayer = new MediaPlayer();

                try {

                    mediaPlayer.setDataSource(file.getPath());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });
    }
}
