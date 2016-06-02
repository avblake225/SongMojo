package com.tonyblake.songmojo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

public class DownloadAudio extends AppCompatActivity{

    private Context context;

    private Button btn_download, btn_play;

    private File file;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_audio);

        context = this;

        btn_download = (Button)findViewById(R.id.btn_download);

        btn_play = (Button)findViewById(R.id.btn_play);

        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Home.recordingRef.getName());
    }

    @Override
    protected void onResume(){
        super.onResume();

        btn_download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Home.recordingRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(context, "Successfully downloaded audio", Toast.LENGTH_SHORT);
                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mediaPlayer = new MediaPlayer();

                try {

                    mediaPlayer.setDataSource(file.getPath());
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

                mediaPlayer.start();

                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });
    }
}
