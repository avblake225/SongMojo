package com.tonyblake.songmojo;

import android.content.Context;
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

public class DownloadAudio extends AppCompatActivity{

    private Context context;

    private Button btn_download;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_audio);

        context = this;

        btn_download = (Button)findViewById(R.id.btn_download);

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

                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                Toast.makeText(context, "Successfully downloaded audio", Toast.LENGTH_SHORT);
            }
        });
    }
}
