package com.tonyblake.songmojo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class GetFileTask extends AsyncTask<String,Void,String>{

    private String filename;

    private Context context;

    private FirebaseStorage storage;

    private StorageReference storageRef;

    public GetFileTask(String filename, Context context){

        this.filename = filename;

        this.context = context;

        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReferenceFromUrl("gs://songmojo.appspot.com");
    }

    @Override
    protected String doInBackground(String... params) {

        final String filenameWithPrefix = filename + context.getString(R.string._mp3);

        StorageReference recordingRef = storageRef.child(filenameWithPrefix);

        File file = new File(Home.filesReceivedDirectory + File.separator + filenameWithPrefix);

        recordingRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Log.i("GetFileTask: ", "Successfully downloaded received file");

            }
        }).
        addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(Exception e) {

                Log.i("GetFileTask: ", "Error downloadeding received file");
            }
        });

        return filename;
    }
}
