package com.tonyblake.songmojo;

import android.app.Dialog;
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

public class FoundBandMemberDialog extends DialogFragment {

    private View view;

    private TextView tv_success_msg;

    private WindowManager.LayoutParams lp;

    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        message = getArguments().getString("message");

        view = inflater.inflate(R.layout.success_dialog, null);

        builder.setTitle(R.string.success)
                .setView(view)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {

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

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tv_success_msg = (TextView) view.findViewById(R.id.tv_success_msg);

        tv_success_msg.setText(message);
    }
}
