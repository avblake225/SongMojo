package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements FindBandMemberDialog.FindBandMemberDialogInterface{

    private Context context;

    public static DBManager dbManager;

    private Toolbar actionBar;

    private DrawerLayout dLayout;
    private ListView dList;
    private ArrayAdapter<String> drawerAdapter;

    private Intent intent;

    private String user, first_name, last_name;

    private LayoutInflater layoutInflater;

    private TextView tv_user, tv_current_date;

    private ArrayList<RecentActivity> recentActivityList;

    private FindBandMemberDialog findBandMemberDialog;

    private FragmentManager fm;

    public static File songMojoDirectory, filesReceivedDirectory, recordingsDirectory;

    private LinearLayout recent_activity_layout_container;

    private ProgressDialog searchingForBandMemberDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        context = this;

        dbManager = new DBManager(context);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tv_user = (TextView)findViewById(R.id.tv_user);

        tv_current_date = (TextView)findViewById(R.id.tv_current_date);

        tv_current_date.setText(Utils.getCurrentDate());

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

        songMojoDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.songmojo));
        songMojoDirectory.mkdirs();

        filesReceivedDirectory = new File(songMojoDirectory + File.separator + context.getString(R.string.files_received));
        filesReceivedDirectory.mkdirs();

        recordingsDirectory = new File(songMojoDirectory + File.separator + context.getString(R.string.recordings));
        recordingsDirectory.mkdirs();

        recent_activity_layout_container = (LinearLayout)findViewById(R.id.recent_activity_layout_container);

        getUserDetails();

        tv_user.setText(first_name);
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

        checkForAvailableFile();

        displayRecentActivity();

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

                        intent.putExtra("user", user);

                        startActivity(intent);

                        break;

                    // Find Band Members
                    case 1:

                        dLayout.closeDrawer(dList);

                        findBandMemberDialog = new FindBandMemberDialog();

                        Bundle bundle = new Bundle();

                        bundle.putString("user", user);

                        findBandMemberDialog.setArguments(bundle);

                        findBandMemberDialog.show(fm, "findBandMemberDialog");

                        break;

                    // Edit Band Members
                    case 2:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(context, EditBandMembers.class);

                        intent.putExtra("user", user);

                        startActivity(intent);

                        break;

                    // Files sent
                    case 3:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(context, FilesSent.class);

                        intent.putExtra("user", user);

                        startActivity(intent);

                        break;

                    // Files received
                    case 4:

                        dLayout.closeDrawer(dList);

                        intent = new Intent(context, FilesReceived.class);

                        startActivity(intent);

                        break;
                }
            }
        });
    }

    @Override
    public void onFindBandMemberDialogOkButtonClick(DialogFragment dialog, final String fullname_entered) {

        new FindBandMemberTask(fullname_entered){

            @Override
            protected void onPreExecute() {

                searchingForBandMemberDialog = new ProgressDialog(context);
                searchingForBandMemberDialog.setIndeterminate(true);
                searchingForBandMemberDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                searchingForBandMemberDialog.setMessage(context.getString(R.string.searching_for_band_member));
                searchingForBandMemberDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean success){

                searchingForBandMemberDialog.dismiss();

                String msg = "";

                if(success){

                    String[] names = Utils.separateWords(fullname_entered);

                    String firstName  = names[0].substring(0, 1).toUpperCase()+ names[0].substring(1).toLowerCase();

                    String lastName  = names[1].substring(0, 1).toUpperCase()+ names[1].substring(1).toLowerCase();

                    String fullname_camelCase = firstName + " " + lastName;

                    ArrayList<String> bandMembers = Utils.getBandMembers(context, dbManager);

                    if(!bandMembers.contains(fullname_camelCase)){

                        dbManager.insertDataIntoBandMembersTable(fullname_camelCase);

                        FoundBandMemberDialog foundBandMemberDialog = new FoundBandMemberDialog();

                        Bundle bundle = new Bundle();

                        msg = fullname_camelCase + " added to band members";

                        bundle.putString("message", msg);

                        foundBandMemberDialog.setArguments(bundle);

                        foundBandMemberDialog.show(fm,"foundBandMemberDialog");
                    }
                    else{

                        msg = fullname_camelCase + " already added to band members";

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                    msg = context.getString(R.string.no_band_member_found);

                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void displayRecentActivity(){

        recent_activity_layout_container.removeAllViews();

        recentActivityList = new ArrayList<>();

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.RECENT_ACTIVITY_TABLE() + " "
                        + context.getString(R.string.where_date_equals) + "'" + Utils.getCurrentDate() + "';";

        Cursor cursor;

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do{

                RecentActivity recentActivity = new RecentActivity();

                recentActivity.time = cursor.getString(2);
                recentActivity.action = cursor.getString(3);

                recentActivityList.add(recentActivity);
            }
            while(cursor.moveToNext());
        }
        catch(Exception e){

            Log.e("LocalDB", "No recent activity data");
        }

        if(recentActivityList.size() == 0){

            View no_files_sent_or_received = layoutInflater.inflate(R.layout.no_files_sent_or_received, null);

            recent_activity_layout_container.addView(no_files_sent_or_received);
        }
        else{

            for(RecentActivity recentActivity: recentActivityList){

                View recent_activity_layout = layoutInflater.inflate(R.layout.recent_activity_layout, null);

                RecentActivityLayout recentActivityLayout = new RecentActivityLayout(recent_activity_layout, recent_activity_layout_container);

                recentActivityLayout.setTime(recentActivity.time);

                recentActivityLayout.setAction(recentActivity.action);

                recentActivityLayout.finish();
            }
        }

        // Delete old activity
        int numRowsBefore = dbManager.getRowsInRecentActivityTable(context); // debug only

        dbManager.deleteOldActivity(Utils.getCurrentDate());

        int numRowsAfter = dbManager.getRowsInRecentActivityTable(context); // debug only
    }

    private class RecentActivity{

        String time;
        String action;
    }

    private void checkForAvailableFile(){

        String filename = "", status = "";

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.FILE_AVAILABLE_TABLE() + ";";

        Cursor cursor;

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do{
                filename = cursor.getString(1);

                status = cursor.getString(2);
            }
            while(cursor.moveToNext());
        }
        catch(Exception e){

            Log.e("CheckForAvailableFile: ", "Error querying File Available table");
        }

        if(status.equals("Available")){

            new GetFileTask(filename,context){

                @Override
                protected void onPostExecute(String filename){

                    dbManager.deleteOldAvailableFile(filename);

                }
            }.execute();
        }
    }

    private void getUserDetails(){

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.USER_DETAILS_TABLE() + ";";

        Cursor cursor;

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do{

                first_name = cursor.getString(1);
                last_name = cursor.getString(2);

                user = first_name + " " + last_name;
            }
            while(cursor.moveToNext());
        }
        catch(Exception e){

            Log.e("getUserDetails: ", "Error retrieving user details");
        }
    }
}
