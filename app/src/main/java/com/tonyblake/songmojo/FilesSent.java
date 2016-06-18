package com.tonyblake.songmojo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FilesSent extends AppCompatActivity{

    private Context context;

    private LinearLayout files_sent_list;

    private LayoutInflater inflator;

    private View sent_file_item_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_sent);

        context = this;

        files_sent_list = (LinearLayout)findViewById(R.id.files_sent_list);

        inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        sent_file_item_layout = inflator.inflate(R.layout.sent_file_item_layout,null);
    }

    @Override
    protected void onResume(){
        super.onResume();

        SentFile sentFile = new SentFile(files_sent_list, sent_file_item_layout);

        sentFile.setRecipient("Cormac");
        sentFile.setFileName("newSong.mp3");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        sentFile.setDate(currentDateandTime);
        sentFile.finish();
    }
}
