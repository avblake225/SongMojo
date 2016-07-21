package com.tonyblake.songmojo;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditBandMembers extends AppCompatActivity implements ClearAllDialog.ClearAllDialogInterface{

    private Context context;

    private Toolbar appBar;

    private DBManager dbManager;

    private TextView tv_screen_title;

    private Cursor cursor;

    private Button btn_clear_all;

    private FragmentManager fm;

    private ArrayList<BandMember> bandMembersToDisplay;

    private ListView band_members_list;

    private EditBandMembers editBandMembers;

    private BandMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_band_members);

        context = this;

        fm = getSupportFragmentManager();

        bandMembersToDisplay = new ArrayList<>();

        band_members_list = (ListView)findViewById(R.id.band_members_list);

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
        tv_screen_title.setText(context.getString(R.string.edit_band_members));

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

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.BAND_MEMBERS_TABLE() + ";";

        if(bandMembersToDisplay.size() == 0){

            getBandMembers(query);
        }
    }

    private void getBandMembers(String query){

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do {

                BandMember bandMember = new BandMember();

                bandMember.name = cursor.getString(1);

                bandMembersToDisplay.add(bandMember);
            }
            while (cursor.moveToNext());
        }
        catch(Exception e){

        }

        editBandMembers = this;

        Resources res = getResources();

        adapter = new BandMemberAdapter(editBandMembers , bandMembersToDisplay, res);

        band_members_list.setAdapter(adapter);
    }

    @Override
    public void onYesButtonClick(DialogFragment dialog) {

        if(bandMembersToDisplay.size() != 0){

            band_members_list.removeAllViews();

            dbManager.deleteBandMembers();

            Toast.makeText(context,context.getString(R.string.band_members_cleared), Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(context,context.getString(R.string.nothing_to_clear), Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemClick(int mPosition) {

        BandMember bandMember = bandMembersToDisplay.get(mPosition);
    }
}
