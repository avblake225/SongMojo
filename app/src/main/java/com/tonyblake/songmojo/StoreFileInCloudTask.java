package com.tonyblake.songmojo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class StoreFileInCloudTask extends AsyncTask<String,Void,Void>{

    private InputStream stream;
    private StorageReference recordingRef;

    public StoreFileInCloudTask(InputStream stream, StorageReference recordingRef){

        this.stream = stream;
        this.recordingRef = recordingRef;
    }

    @Override
    protected Void doInBackground(String... params) {

        UploadTask uploadTask = recordingRef.putStream(stream);

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.i("StoreFileInCloudTask","Error storing file in cloud");

            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.i("StoreFileInCloudTask","Successfully stored file in cloud");
            }
        });

        return null;
    }
}
