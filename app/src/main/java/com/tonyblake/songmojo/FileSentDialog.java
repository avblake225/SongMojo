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
import android.widget.Button;
import android.widget.TextView;

public class FileSentDialog extends DialogFragment {

    private Context context;
    private View view;
    private TextView tv_file_sent;
    private WindowManager.LayoutParams lp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.file_sent_dialog, null);

        builder.setTitle(R.string.file_sent)
                .setView(view)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        fileSentDialogInterface.onDoneButtonClick(FileSentDialog.this);
                    }
                });

        final AlertDialog dialog = builder.create();

        lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.getWindow().setAttributes(lp);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button doneButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                doneButton.setTextColor(Color.BLACK);

                final Drawable searchButtonBackground = getResources().getDrawable(R.drawable.background_color);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    doneButton.setBackground(searchButtonBackground);
                }
            }
        });

        return dialog;
    }

    public interface FileSentDialogInterface {

        void onDoneButtonClick(DialogFragment dialog);
    }

    FileSentDialogInterface fileSentDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            fileSentDialogInterface = (FileSentDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tv_file_sent = (TextView) view.findViewById(R.id.tv_file_sent);

        String message = "";

        if(SendFile.audioFile){

            message = RecordAudio.filename + " sent to " + RecordAudio.recipient;
        }
        else if(SendFile.videoFile){

            message = RecordVideo.filename + " sent to " + RecordVideo.recipient;
        }

        tv_file_sent.setText(message);
    }
}
