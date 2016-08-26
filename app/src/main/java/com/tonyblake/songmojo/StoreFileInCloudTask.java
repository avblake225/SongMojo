package com.tonyblake.songmojo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class StoreFileInCloudTask extends AsyncTask<String,Void,Boolean>{

    private Context context;
    private InputStream stream;
    private StorageReference recordingRef;

    private Boolean result;

    public StoreFileInCloudTask(Context context, InputStream stream, StorageReference recordingRef){

        this.context = context;
        this.stream = stream;
        this.recordingRef = recordingRef;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        result = false;

        UploadTask uploadTask = recordingRef.putStream(stream);

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(context, context.getString(R.string.error_downloading_file), Toast.LENGTH_SHORT).show();

            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                result = true;

                Log.i("StoreFileInCloudTask","Successfully stored file in cloud");
            }
        });

        return result;
    }
}
