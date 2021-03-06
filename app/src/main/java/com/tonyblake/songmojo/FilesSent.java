package com.tonyblake.songmojo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FilesSent extends AppCompatActivity implements ClearAllDialog.ClearAllDialogInterface{

    private Context context;

    private String user;

    private Toolbar appBar;

    private DBManager dbManager;

    private TextView tv_screen_title;

    private LinearLayout files_sent_list;

    private LayoutInflater inflator;

    private View sent_file_item_layout;

    private Cursor cursor;

    private ArrayList<SentFile> sentFiles;

    private Button btn_clear_all;

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_sent_or_received);

        context = this;

        savedInstanceState = getIntent().getExtras();

        user = savedInstanceState.getString("user");

        fm = getSupportFragmentManager();

        // Show Status Bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        // Set up App bar
        appBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(appBar);
        appBar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        appBar.setTitle(context.getString(R.string.app_name));
        appBar.setTitleTextColor(context.getResources().getColor(R.color.white));

        dbManager = Home.dbManager;

        tv_screen_title = (TextView)findViewById(R.id.screen_title);
        tv_screen_title.setText(context.getString(R.string.files_sent));

        files_sent_list = (LinearLayout)findViewById(R.id.files_sent_or_received_list);

        inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        sentFiles = new ArrayList<>();

        btn_clear_all = (Button)findViewById(R.id.btn_clear_all);
    }

    @Override
    protected void onResume(){
        super.onResume();

        btn_clear_all.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ClearAllDialog clearAllDialog = new ClearAllDialog();
                clearAllDialog.show(fm, "clearAllDialog");
            }
        });

        appBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.FILES_SENT_TABLE() + " "
                        + context.getString(R.string.where_user_equals) + "'" + user + "';";

        if(sentFiles.size() == 0){

            getSentFiles(query);
        }
    }

    private void getSentFiles(String query){

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do {

                SentFile sentFile = new SentFile();

                sentFile.recipient = cursor.getString(2);

                sentFile.filename = cursor.getString(3);

                sentFile.duration = cursor.getString(4);

                sentFile.filetype = cursor.getString(5);

                sentFile.date = cursor.getString(6);

                sentFiles.add(sentFile);
            }
            while (cursor.moveToNext());
        }
        catch(Exception e){

        }

        for(SentFile sentFile: sentFiles){

            sent_file_item_layout = inflator.inflate(R.layout.sent_file_item_layout,null);

            SentFileListItem sentFileListItem = new SentFileListItem(files_sent_list, sent_file_item_layout);

            sentFileListItem.setRecipient(sentFile.recipient);
            sentFileListItem.setFileName(sentFile.filename);
            sentFileListItem.setDuration(sentFile.duration);
            sentFileListItem.setFileType(sentFile.filetype);
            sentFileListItem.setDate(sentFile.date);
            sentFileListItem.finish();

            sent_file_item_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    PlayAudioDialog playAudioDialog = new PlayAudioDialog();

                    TextView tv_file_name = (TextView)sent_file_item_layout.findViewById(R.id.tv_file_name);

                    String filename = tv_file_name.getText().toString();

                    String filePath = Home.recordingsDirectory + File.separator + filename;

                    Bundle bundle = new Bundle();

                    bundle.putString("filename", filename);

                    bundle.putString("filepath", filePath);

                    playAudioDialog.setArguments(bundle);

                    playAudioDialog.show(fm, "playAudioDialog");
                }
            });
        }
    }

    @Override
    public void onYesButtonClick(DialogFragment dialog) {

        if(sentFiles.size() != 0){

            files_sent_list.removeAllViews();

            dbManager.deleteSentFiles();

            Toast.makeText(context,context.getString(R.string.files_cleared), Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(context,context.getString(R.string.nothing_to_clear), Toast.LENGTH_SHORT).show();
        }
    }

    private class SentFile{

        private String recipient;
        private String filename;
        private String duration;
        private String filetype;
        private String date;
    }
}
