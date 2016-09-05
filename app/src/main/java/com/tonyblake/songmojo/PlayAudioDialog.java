package com.tonyblake.songmojo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PlayAudioDialog extends DialogFragment {

    private TextView tv_filename;

    private Button pause, play;

    private Animation animation;

    private View view;

    private WindowManager.LayoutParams lp;

    public MediaPlayer audioPlayer;

    private ImageView ic_audiotrack;

    private PausableChronometer pausableChronometer;

    private String filename, filepath;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade);

        filename = getArguments().get("filename").toString();

        filepath = getArguments().get("filepath").toString();

        audioPlayer = new MediaPlayer();

        startAudioPlayer();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.play_dialog, null);

        builder.setTitle(R.string.playing)
                .setView(view)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        try{

                            audioPlayer.stop();
                            audioPlayer.reset();

                            dismiss();
                        }
                        catch(IllegalStateException e){

                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
                        }
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

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tv_filename = (TextView)view.findViewById(R.id.tv_filename);
        tv_filename.setText(filename);

        pause = (Button) view.findViewById(R.id.btn_pause);
        pause.setBackgroundResource(android.R.drawable.btn_default);

        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                if (audioPlayer.isPlaying()) {

                    audioPlayer.pause();
                    pausableChronometer.stop();
                    animation.cancel();
                }

                play.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        play = (Button) view.findViewById(R.id.btn_play);
        play.setBackgroundResource(android.R.drawable.btn_default);
        play.setEnabled(false);

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                try {

                    audioPlayer.start();
                }
                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                pausableChronometer.start();
                ic_audiotrack.startAnimation(animation);

                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        // Set up animation
        ic_audiotrack = (ImageView)view.findViewById(R.id.ic_audiotrack);
        ic_audiotrack.startAnimation(animation);

        // Set up timer
        pausableChronometer = new PausableChronometer();
        pausableChronometer.start();

        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                animation.cancel();
                pausableChronometer.reset();
                pause.setEnabled(false);
                play.setEnabled(true);
            }
        });
    }

    private void startAudioPlayer(){

        try {

            audioPlayer.setDataSource(filepath);
            audioPlayer.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {

            audioPlayer.start();
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class PausableChronometer{

        private Chronometer chronometer;

        private long timeWhenStopped = 0;

        public PausableChronometer(){

            chronometer = (Chronometer) view.findViewById(R.id.my_chronometer);
        }

        public void start() {

            chronometer.setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
            chronometer.start();
        }

        public void stop() {

            chronometer.stop();
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        }

        public void reset() {

            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenStopped = 0;
        }
    }
}
