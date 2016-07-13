package com.tonyblake.songmojo;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class RecordAudioDialog extends DialogFragment {

    private View view;

    private WindowManager.LayoutParams lp;

    public static MediaRecorder audioRecorder;

    private ImageView ic_mic;

    private Chronometer chronometer;

    private String filepath;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        filepath = getArguments().get("filepath").toString();

        audioRecorder = new MediaRecorder();

        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioRecorder.setOutputFile(filepath);

        try {

            audioRecorder.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {

            audioRecorder.start();
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.record_dialog, null);

        builder.setTitle(R.string.recording)
                .setView(view)
                .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        try{

                            audioRecorder.stop();
                            audioRecorder.reset();
                        }
                        catch(IllegalStateException e){

                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
                        }

                        chronometer.stop();

                        recordDialogInterface.onStopButtonClick(RecordAudioDialog.this);
                    }
                });

        final AlertDialog dialog = builder.create();

        lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.getWindow().setAttributes(lp);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button stopButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                stopButton.setTextColor(Color.BLACK);

                final Drawable searchButtonBackground = getResources().getDrawable(R.drawable.background_color);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    stopButton.setBackground(searchButtonBackground);
                }
            }
        });

        return dialog;
    }

    public interface RecordDialogInterface{

        void onStopButtonClick(DialogFragment dialog);
    }

    RecordDialogInterface recordDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            recordDialogInterface = (RecordDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Set up animation
        ic_mic = (ImageView)view.findViewById(R.id.ic_mic);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade);
        ic_mic.startAnimation(animation);

        // Set up timer
        chronometer = (Chronometer)view.findViewById(R.id.my_chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }
}
