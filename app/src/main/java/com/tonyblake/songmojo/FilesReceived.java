package com.tonyblake.songmojo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
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

public class FilesReceived extends AppCompatActivity implements ClearAllDialog.ClearAllDialogInterface{

    private Context context;

    private Toolbar appBar;

    private DBManager dbManager;

    private TextView tv_screen_title;

    private LinearLayout files_received_list;

    private LayoutInflater inflator;

    private View received_file_item_layout;

    private Cursor cursor;

    private ArrayList<ReceivedFile> receivedFiles;

    private Button btn_clear_all;

    private FragmentManager fm;

    private String downloadsFolderPath;

    private File downloadsFolder;

    private File[] downloads;

    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_sent_or_received);

        context = this;

        fm = getSupportFragmentManager();

        savedInstanceState = getIntent().getExtras();

        user = savedInstanceState.getString("user");

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
        tv_screen_title.setText(context.getString(R.string.files_received));

        files_received_list = (LinearLayout)findViewById(R.id.files_sent_or_received_list);

        inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        receivedFiles = new ArrayList<>();

        btn_clear_all = (Button)findViewById(R.id.btn_clear_all);

        downloadsFolderPath = Environment.getExternalStorageDirectory() + File.separator + "SongMojo" + File.separator + "Downloads";

        downloadsFolder = new File(downloadsFolderPath);

        downloads = downloadsFolder.listFiles();
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

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.FILES_DOWNLOADED_TABLE() + " "
                        + context.getString(R.string.where_recipient_equals) + "'" + user + "';";

        if(receivedFiles.size() == 0){

            getReceivedFiles(query);
        }
    }

    private void getReceivedFiles(String query){

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do {

                ReceivedFile receivedFile = new ReceivedFile();

                receivedFile.sender = cursor.getString(1);

                receivedFile.filename = cursor.getString(3);

                receivedFile.duration = cursor.getString(4);

                receivedFile.filetype = cursor.getString(5);

                receivedFile.date = cursor.getString(6);

                receivedFiles.add(receivedFile);
            }
            while (cursor.moveToNext());
        }
        catch(Exception e){

        }

        for(ReceivedFile file: receivedFiles){

            received_file_item_layout = inflator.inflate(R.layout.received_file_item_layout,null);

            ReceivedFileListItem receivedFileListItem = new ReceivedFileListItem(files_received_list, received_file_item_layout);

            receivedFileListItem.setSender(file.sender);
            receivedFileListItem.setFileName(file.filename);
            receivedFileListItem.setDate(file.date);
            receivedFileListItem.setFileType(file.filetype);
            receivedFileListItem.setDuration(file.duration);

            receivedFileListItem.finish();
        }
    }

    @Override
    public void onYesButtonClick(DialogFragment dialog) {

        if(receivedFiles.size() != 0){

            files_received_list.removeAllViews();

            dbManager.deleteDownloadedFiles();

            // Remove files from external memory on device
            for(File file: downloads){

                file.delete();
            }

            Toast.makeText(context,context.getString(R.string.files_cleared), Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(context,context.getString(R.string.nothing_to_clear), Toast.LENGTH_SHORT).show();
        }
    }

    private class ReceivedFile{

        private String sender;
        private String filename;
        private String date;
        private String filetype;
        private String duration;
    }
}
