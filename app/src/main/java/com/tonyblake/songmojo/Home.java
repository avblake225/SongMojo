package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements GetFileDialog.GetFileDialogInterface{

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    private ArrayList<AvailableFile> availableFiles;

    private ArrayList<String> availableFilenames;

    private Context context;

    public static DBManager dbManager;

    private Toolbar actionBar;

    private DrawerLayout dLayout;
    private ListView dList;
    private ArrayAdapter<String> drawerAdapter;

    private Intent intent;

    private LinearLayout layout_container;

    private String firstName;

    private LayoutInflater layoutInflater;

    private TextView tv_user;

    private GetFileDialog getFileDialog;

    private ProgressDialog getFileProgressDialog;

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        database = FirebaseDatabase.getInstance();

        databaseRef = database.getReference().child("files");

        context = this;

        dbManager = new DBManager(context);

        savedInstanceState = getIntent().getExtras();

        firstName = savedInstanceState.getString("firstName");

        layout_container = (LinearLayout)findViewById(R.id.layout_container);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tv_user = (TextView)findViewById(R.id.tv_user);

        tv_user.setText(firstName);

        createWelcomeMessage();

        // Show Status Bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        // Set up Action Bar
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_menu_white_24dp));
        actionBar.setTitle(context.getString(R.string.app_name));
        actionBar.setTitleTextColor(context.getResources().getColor(R.color.white));
        actionBar.setOverflowIcon(context.getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));

        // Set up Navigation Drawer
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        drawerAdapter = new ArrayAdapter<>(this,R.layout.drawer_item_layout,context.getResources().getStringArray(R.array.menu_items));
        dList.setAdapter(drawerAdapter);

        fm = getSupportFragmentManager();
    }

    private void createWelcomeMessage(){

        View welcome_layout = layoutInflater.inflate(R.layout.welcome, null);

        TextView tv = (TextView)welcome_layout.findViewById(R.id.tv_welcome);

        String welcome_message = context.getString(R.string.welcome_message);

        tv.setText(welcome_message);

        layout_container.addView(welcome_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.overflow_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {

            case R.id.log_out:

                Intent intent = new Intent(this, Login.class);

                startActivity(intent);

                finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        availableFiles = new ArrayList<>();

        availableFilenames = new ArrayList<>();

        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue(User.class) != null) {

                    for (DataSnapshot userID : dataSnapshot.getChildren()) {

                        AvailableFile availableFile = new AvailableFile();

                        availableFile.sender = (String) userID.child("sender").getValue();
                        availableFile.filename = (String) userID.child("filename").getValue();
                        availableFile.recipient = (String) userID.child("recipient").getValue();

                        availableFiles.add(availableFile);
                    }
                }

                for (AvailableFile availableFile : availableFiles) {

                    availableFilenames.add(availableFile.filename);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        actionBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dLayout.openDrawer(dList);
            }
        });

        dList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fm = getSupportFragmentManager();

                switch (position) {

                    // Send File
                    case 0:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(context, SendFile.class);

                        intent.putExtra("firstName", firstName);
                        intent.putExtra("availableFilenames", availableFilenames);

                        startActivity(intent);

                        break;

                    // Get File
                    case 1:

                        dLayout.closeDrawer(dList);

                        getFileDialog = new GetFileDialog();

                        Bundle bundle = new Bundle();

                        bundle.putStringArrayList("availableFilenames", availableFilenames);

                        getFileDialog.setArguments(bundle);

                        getFileDialog.show(fm, "getFileDialog");

                        break;

                    // Find Band Members
                    case 2:

                        dLayout.closeDrawer(dList);

                        Toast.makeText(context, "Feature currently unavailable", Toast.LENGTH_SHORT).show();
                        ;

                        break;

                    // Edit Band Members
                    case 3:

                        dLayout.closeDrawer(dList);

                        Toast.makeText(context, "Feature currently unavailable", Toast.LENGTH_SHORT).show();
                        ;

                        break;

                    // Files sent
                    case 4:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(context, FilesSent.class);

                        startActivity(intent);

                        break;

                    // Files received
                    case 5:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(context, FilesReceived.class);

                        startActivity(intent);

                        break;
                }
            }
        });
    }

    @Override
    public void onGetFileDialogOkButtonClick(DialogFragment dialog, final String filename) {

        if(Utils.connectedToNetwork(context)){

            new GetFileTask(filename,context){

                @Override
                protected void onPreExecute() {

                    getFileProgressDialog = new ProgressDialog(context);
                    getFileProgressDialog.setIndeterminate(true);
                    getFileProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    getFileProgressDialog.setMessage(context.getString(R.string.downloading_file));
                    getFileProgressDialog.show();
                }

                @Override
                protected void onPostExecute(String result) {

                    getFileProgressDialog.dismiss();

                    DownloadSuccessDialog downloadSuccessDialog = new DownloadSuccessDialog();

                    Bundle bundle = new Bundle();

                    bundle.putString("filename", filename);

                    downloadSuccessDialog.setArguments(bundle);

                    downloadSuccessDialog.show(fm, "downloadSuccessDialog");
                }
            }.execute();
        }
        else{

            Utils.showToastMessage(context, context.getString(R.string.no_network_connection));
        }
    }

    private void displayAudioScreen(String filename){

        final File file = new File(filename);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View download_audio_layout = inflater.inflate(R.layout.download_audio, null);

        layout_container.addView(download_audio_layout);

        TextView tv_file_name = (TextView) download_audio_layout.findViewById(R.id.tv_file_name);

        Button btn_play = (Button) download_audio_layout.findViewById(R.id.btn_play);

        tv_file_name.setText(filename + ".3pg");

        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MediaPlayer mediaPlayer = new MediaPlayer();

                try {

                    mediaPlayer.setDataSource(file.getPath());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });
    }
}
