package com.tonyblake.songmojo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

    private View view;

    private ArrayList<String> files;

    private Spinner backing_track_spinner;

    private ArrayAdapter<String> backing_track_spinnerAdapter;

    private WindowManager.LayoutParams lp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.cue_backing_track_dialog, null);

        builder.setTitle(R.string.select_track)
                .setView(view)
                .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String fileChosen = backing_track_spinner.getSelectedItem().toString();

                        cueBackingTrackDialogInterface.onSelectButtonClick(CueBackingTrackDialog.this, fileChosen);
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
                Button selectButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                cancelButton.setTextColor(Color.BLACK);
                selectButton.setTextColor(Color.BLACK);

                final Drawable cancelButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cancelButton.setBackground(cancelButtonBackground);
                }

                final Drawable searchButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    selectButton.setBackground(searchButtonBackground);
                }
            }
        });

        return dialog;
    }

    public interface CueBackingTrackDialogInterface {

        void onSelectButtonClick(DialogFragment dialog, String fileChosen);
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

        files = new ArrayList<>();

        files.add("some file 1");
        files.add("some file 2");
        files.add("some file 3");

        backing_track_spinner = (Spinner) view.findViewById(R.id.backing_track_spinner);

        backing_track_spinnerAdapter = new ArrayAdapter<>(context, R.layout.my_custom_spinner, files);

        backing_track_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        backing_track_spinner.setAdapter(backing_track_spinnerAdapter);
    }
}
