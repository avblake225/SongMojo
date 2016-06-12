package com.tonyblake.songmojo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class SendFileTask extends AsyncTask<String,Void,String>{

    private InputStream stream;
    private StorageReference recordingRef;
    private String fileStatus;

    public SendFileTask(InputStream stream, StorageReference recordingRef){

        this.stream = stream;
        this.recordingRef = recordingRef;
    }

    @Override
    protected String doInBackground(String... params) {

        fileStatus = "sending";

        UploadTask uploadTask = recordingRef.putStream(stream);

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileStatus = "sent";
            }
        });

        return fileStatus;
    }
}
