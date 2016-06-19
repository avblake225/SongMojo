package com.tonyblake.songmojo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class FilesSent extends AppCompatActivity{

    private Context context;

    private DBManager dbManager;

    private LinearLayout files_sent_list;

    private LayoutInflater inflator;

    private View sent_file_item_layout;

    private Cursor cursor;

    private ArrayList<SentFile> sentFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_sent);

        context = this;

        dbManager = Home.dbManager;

        files_sent_list = (LinearLayout)findViewById(R.id.files_sent_list);

        inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        sent_file_item_layout = inflator.inflate(R.layout.sent_file_item_layout,null);

        sentFiles = new ArrayList<>();
    }

    @Override
    protected void onResume(){
        super.onResume();

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.getTableName() + ";";

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do {

                SentFile sentFile = new SentFile();

                sentFile.recipient = cursor.getString(1);
                sentFile.filename = cursor.getString(2);
                sentFile.date = cursor.getString(3);

                sentFiles.add(sentFile);
            }
            while (cursor.moveToNext());
        }
        catch(Exception e){

            Toast.makeText(context,"No files sent", Toast.LENGTH_SHORT).show();
        }

        for(SentFile sentFile: sentFiles){

            SentFileListItem sentFileListItem = new SentFileListItem(files_sent_list, sent_file_item_layout);

            sentFileListItem.setRecipient(sentFile.recipient);
            sentFileListItem.setFileName(sentFile.filename);
            sentFileListItem.setDate(sentFile.date);
            sentFileListItem.finish();

        }
    }

    private class SentFile{

        private String recipient;
        private String filename;
        private String date;
    }
}
