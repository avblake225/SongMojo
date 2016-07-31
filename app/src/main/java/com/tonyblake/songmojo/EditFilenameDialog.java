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
import android.widget.EditText;

public class EditFilenameDialog extends DialogFragment {

    private Context context;

    private WindowManager.LayoutParams lp;

    private LayoutInflater inflator;

    private View layout;

    private EditText tv_new_filename;

    private String new_filename;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout = inflator.inflate(R.layout.edit_filename_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(layout)
                .setTitle(context.getString(R.string.edit_filename))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        new_filename = tv_new_filename.getText().toString();

                        editFilenameDialogInterface.onEditFilenameDialogOkButtonClick(EditFilenameDialog.this, new_filename);

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

    public interface EditFilenameDialogInterface{

        void onEditFilenameDialogOkButtonClick(DialogFragment dialog, String new_filename);
    }

    EditFilenameDialogInterface editFilenameDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            editFilenameDialogInterface = (EditFilenameDialogInterface)activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tv_new_filename = (EditText) layout.findViewById(R.id.tv_new_filename);
    }
}
