package com.tonyblake.songmojo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SendFileTask extends AsyncTask<String,Void,String>{

    private String filePath;
    private StorageReference recordingRef;
    private String fileStatus;

    public SendFileTask(String filePath, StorageReference recordingRef){

        this.filePath = filePath;
        this.recordingRef = recordingRef;
    }

    @Override
    protected String doInBackground(String... params) {

        fileStatus = "sending";

        InputStream stream = null;

        try {

            stream = new FileInputStream(new File(filePath));

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
