package com.tonyblake.songmojo;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindBandMemberTask extends AsyncTask<String,Void,Boolean> {

    String fullname;

    public FindBandMemberTask(String fullname){

        this.fullname = fullname;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Boolean result = false;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("FullName", fullname)
                .build();

        Request request = new Request.Builder()
                .url("http://tonyonandroid.com/songmojo/findbandmember.php")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String bandMemberReturned = response.body().string(); // for debugging only

            int response_code = response.code();

            if(response_code == 200 && !("".equals(bandMemberReturned))){

                result = true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
