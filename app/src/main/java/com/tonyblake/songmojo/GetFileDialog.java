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
import android.widget.TextView;

import java.util.ArrayList;

public class GetFileDialog extends DialogFragment {

    private Context context;

    private ArrayList<String> availableFilenames;

    private View view;

    private TextView tv_available_files;

    private Spinner select_track_spinner;

    private ArrayAdapter<String> select_track_spinnerAdapter;

    private WindowManager.LayoutParams lp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        availableFilenames = getArguments().getStringArrayList("availableFilenames");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.spinner_dialog, null);

        builder.setTitle(R.string.select_track);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (availableFilenames.size() == 0) {

                            dismiss();
                        } else {

                            String fileChosen = select_track_spinner.getSelectedItem().toString();

                            getFileDialogInterface.onGetFileDialogOkButtonClick(GetFileDialog.this, fileChosen);
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

    public interface GetFileDialogInterface {

        void onGetFileDialogOkButtonClick(DialogFragment dialog, String fileChosen);
    }

    GetFileDialogInterface getFileDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            getFileDialogInterface = (GetFileDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tv_available_files = (TextView) view.findViewById(R.id.tv_available_files);

        if(availableFilenames.size() == 0){

            tv_available_files.setText(context.getString(R.string.no_tracks_available));
        }
        else {

            String message;

            int num_files = availableFilenames.size();

            if (num_files == 1) {

                message = num_files + " track available:";
            } else {

                message = num_files + " tracks available:";
            }

            tv_available_files.setText(message);

            select_track_spinner = (Spinner) view.findViewById(R.id.select_item_spinner);

            select_track_spinnerAdapter = new ArrayAdapter<>(context, R.layout.my_custom_spinner, availableFilenames);

            select_track_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            select_track_spinner.setAdapter(select_track_spinnerAdapter);
        }
    }
}
