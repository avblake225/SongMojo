package com.tonyblake.songmojo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditBandMembers extends AppCompatActivity implements DeleteBandMemberDialog.DeleteBandMemberDialogInterface{

    private Context context;

    private String user;

    private Toolbar appBar;

    private DBManager dbManager;

    private TextView tv_screen_title;

    private FragmentManager fm;

    private ArrayList<String> bandMembersToDisplay;

    private ListView band_members_list;

    private EditBandMembers editBandMembers;

    private BandMemberAdapter adapter;

    private DeleteBandMemberDialog deleteBandMemberDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_band_members);

        context = this;

        savedInstanceState = getIntent().getExtras();

        user = savedInstanceState.getString("user");

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
    }

    @Override
    protected void onResume(){
        super.onResume();

        appBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        if(bandMembersToDisplay.size() == 0){

            bandMembersToDisplay = Utils.getBandMembers(context, dbManager);

            if(bandMembersToDisplay.size() == 0){

                Toast.makeText(context, context.getString(R.string.no_band_members_added), Toast.LENGTH_SHORT).show();
            }
            else{

                editBandMembers = this;

                Resources res = getResources();

                adapter = new BandMemberAdapter(editBandMembers , bandMembersToDisplay, res);

                band_members_list.setAdapter(adapter);
            }
        }
    }

    public void onItemClick(int mPosition) {

        String name = bandMembersToDisplay.get(mPosition);

        deleteBandMemberDialog = new DeleteBandMemberDialog();

        Bundle bundle = new Bundle();

        bundle.putString("bandMember", name);
        bundle.putInt("position", mPosition);

        deleteBandMemberDialog.setArguments(bundle);

        deleteBandMemberDialog.show(fm,"deleteBandMemberDialog");
    }

    @Override
    public void onDeleteBandMemberYesButtonClick(DialogFragment dialog, String bandMember, int position) {

        dbManager.deleteBandMember(bandMember);

        int numBandMembers = adapter.clearData(position);

        if(numBandMembers == 0){

            band_members_list.setAdapter(null);
        }

        adapter.notifyDataSetChanged(); // refresh view

        String msg = bandMember + " deleted";

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
