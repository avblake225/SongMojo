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
import android.view.WindowManager;
import android.widget.Button;

public class DeleteBandMemberDialog extends DialogFragment {

    private Context context;

    private WindowManager.LayoutParams lp;

    private String bandMember;

    private int position;

    private String delete_msg;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        bandMember = getArguments().getString("bandMember");

        position = getArguments().getInt("position",0);

        delete_msg = "Delete " + bandMember + "?";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(delete_msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        deleteBandMemberDialogInterface.onDeleteBandMemberYesButtonClick(DeleteBandMemberDialog.this, bandMember, position);

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

                Button yesButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                Button cancelButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                yesButton.setTextColor(Color.BLACK);
                cancelButton.setTextColor(Color.BLACK);

                final Drawable yesButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yesButton.setBackground(yesButtonBackground);
                }

                final Drawable cancelBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    cancelButton.setBackground(cancelBackground);
                }
            }
        });

        return dialog;
    }

    public interface DeleteBandMemberDialogInterface {

        void onDeleteBandMemberYesButtonClick(DialogFragment dialog, String bandMember, int position);
    }

    DeleteBandMemberDialogInterface deleteBandMemberDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            deleteBandMemberDialogInterface = (DeleteBandMemberDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }
}
