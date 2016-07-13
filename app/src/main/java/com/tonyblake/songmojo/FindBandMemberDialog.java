package com.tonyblake.songmojo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Button;

public class FindBandMemberDialog extends DialogFragment {

    private Context context;

    private WindowManager.LayoutParams lp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(context.getString(R.string.find_band_member))
                .setMessage("to do...")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dismiss();

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

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

                Button okButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                Button cancelButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                okButton.setTextColor(Color.BLACK);
                cancelButton.setTextColor(Color.BLACK);

                final Drawable yesButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    okButton.setBackground(yesButtonBackground);
                }

                final Drawable cancelBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    cancelButton.setBackground(cancelBackground);
                }
            }
        });

        return dialog;
    }
}
