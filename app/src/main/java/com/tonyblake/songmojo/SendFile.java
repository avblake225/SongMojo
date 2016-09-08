package com.tonyblake.songmojo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SendFile extends AppCompatActivity{

    private Context context;

    private DBManager dbManager;

    private EditText et_file_name;

    private RadioButton rb_audio_file, rb_video_file;

    private Spinner select_recipient_spinner;

    private ArrayAdapter<String> spinnerAdapter;

    private ArrayList<String> bandMembers;

    private String recipient_chosen;

    private Button btn_proceed_to_recording;

    private String user;

    private String filename;

    private Intent intent;

    private Toolbar actionBar;

    public static boolean audioFile, videoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_file);

        context = this;

        dbManager = Home.dbManager;

        savedInstanceState = getIntent().getExtras();

        user = savedInstanceState.getString("user");

        recipient_chosen = null;

        bandMembers = Utils.getBandMembers(context, dbManager);

        et_file_name = (EditText)findViewById(R.id.et_file_name);

        rb_audio_file = (RadioButton)findViewById(R.id.rb_audio_file);

        rb_video_file = (RadioButton)findViewById(R.id.rb_video_file);

        select_recipient_spinner = (Spinner)findViewById(R.id.select_recipient_spinner);

        spinnerAdapter = new ArrayAdapter<>(this, R.layout.my_custom_spinner, bandMembers);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        select_recipient_spinner.setAdapter(spinnerAdapter);

        btn_proceed_to_recording = (Button)findViewById(R.id.btn_proceed_to_recording);

        // Show Status Bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        // Set up Action Bar
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        actionBar.setTitle(context.getString(R.string.app_name));
        actionBar.setTitleTextColor(context.getResources().getColor(R.color.white));
    }

    @Override
    protected void onResume(){
        super.onResume();

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        btn_proceed_to_recording.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                recipient_chosen = select_recipient_spinner.getSelectedItem().toString();

                if("".equals(filename)){

                    Toast.makeText(context, context.getString(R.string.please_set_file_name), Toast.LENGTH_SHORT).show();
                }
                else if(rb_audio_file.isChecked()){

                    audioFile = true;
                    videoFile = false;

                    filename = et_file_name.getText().toString() + context.getString(R.string._mp3);

                    intent = new Intent(context, RecordAudio.class);

                    intent.putExtra("user", user);
                    intent.putExtra("filename", filename);
                    intent.putExtra("recipient", recipient_chosen);

                    startActivity(intent);
                }
                else if(rb_video_file.isChecked()){

                    audioFile = false;
                    videoFile = true;

                    filename = et_file_name.getText().toString() + context.getString(R.string._mp4);

                    intent = new Intent(context, RecordVideo.class);

                    intent.putExtra("user", user);
                    intent.putExtra("filename", filename);
                    intent.putExtra("recipient", recipient_chosen);

                    //startActivity(intent);

                    Toast.makeText(context,"Video currently unavailable",Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(context, context.getString(R.string.please_select_file_type), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
