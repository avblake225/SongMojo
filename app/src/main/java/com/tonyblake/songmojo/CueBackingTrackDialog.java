package com.tonyblake.songmojo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class CueBackingTrackDialog extends DialogFragment {

    private Context context;

    private String user;

    private DBManager dbManager;

    private ArrayList<String> filesDownloaded;

    private View view;

    private Spinner select_track_spinner;

    private ArrayAdapter<String> select_track_spinnerAdapter;

    private WindowManager.LayoutParams lp;

    private AlertDialog.Builder builder;

    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        user = getArguments().getString("user");

        dbManager = Home.dbManager;

        filesDownloaded = new ArrayList<>();

        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.spinner_dialog, null);

        builder.setTitle(R.string.select_track);

        builder.setMessage(message);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (filesDownloaded.size() == 0) {

                            dismiss();
                        }
                        else {

                            String fileChosen = select_track_spinner.getSelectedItem().toString();

                            cueBackingTrackDialogInterface.onCueBackingTrackDialogOkButtonClick(CueBackingTrackDialog.this, fileChosen);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();

        lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.getWindow().setAttributes(lp);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button cancelButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                Button okButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                cancelButton.setTextColor(Color.BLACK);
                okButton.setTextColor(Color.BLACK);

                final Drawable cancelButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cancelButton.setBackground(cancelButtonBackground);
                }

                final Drawable searchButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    okButton.setBackground(searchButtonBackground);
                }
            }
        });

        return dialog;
    }

    public interface CueBackingTrackDialogInterface {

        void onCueBackingTrackDialogOkButtonClick(DialogFragment dialog, String fileChosen);
    }

    CueBackingTrackDialogInterface cueBackingTrackDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            cueBackingTrackDialogInterface = (CueBackingTrackDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        select_track_spinner = (Spinner) view.findViewById(R.id.select_item_spinner);

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.FILES_DOWNLOADED_TABLE() + " "
                        + context.getString(R.string.where_recipient_equals) + "'" + user + "';";

        Cursor cursor = null;

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do{

                filesDownloaded.add(cursor.getString(3));
            }
            while(cursor.moveToNext());
        }
        catch(Exception e){};

        if(filesDownloaded.size() == 0){

            message = context.getString(R.string.no_tracks_available);
        }
        else{

            message = "";

            select_track_spinnerAdapter = new ArrayAdapter<>(context, R.layout.my_custom_spinner, filesDownloaded);

            select_track_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            select_track_spinner.setAdapter(select_track_spinnerAdapter);
        }
    }
}
