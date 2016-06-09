package com.tonyblake.songmojo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class UploadFile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context context;

    private EditText et_file_name;

    private RadioButton rb_audio_file, rb_video_file;

    private Spinner select_recipient_spinner;

    private ArrayAdapter<String> spinnerAdapter;

    private ArrayList<String> recipients;

    private String recipient_chosen;

    private Button btn_proceed_to_recording;

    private String filename;

    private Intent intent;

    private Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_file);

        context = this;

        recipient_chosen = null;

        recipients = new ArrayList<>();

        recipients.add("Simon");
        recipients.add("John");
        recipients.add("Lisa");
        recipients.add("Ben");

        et_file_name = (EditText)findViewById(R.id.et_file_name);

        rb_audio_file = (RadioButton)findViewById(R.id.rb_audio_file);

        rb_video_file = (RadioButton)findViewById(R.id.rb_video_file);

        select_recipient_spinner = (Spinner)findViewById(R.id.select_recipient_spinner);

        spinnerAdapter = new ArrayAdapter<>(this, R.layout.my_custom_spinner, recipients);

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

                showToastMessage("To Do...");

                filename = et_file_name.getText().toString();

                if(rb_audio_file.isChecked()){

                    //intent = new Intent(context, RecordAudio.class);
                }
                else if(rb_video_file.isChecked()){

                    //intent = new Intent(context, RecordVideo.class);
                }

//                intent.putExtra("filename",filename);
//                intent.putExtra("recipient", recipient_chosen);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        recipient_chosen = (String)parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showToastMessage(CharSequence text) {

        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
