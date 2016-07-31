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

public class EditRecipientDialog extends DialogFragment {

    private Context context;

    private ArrayList<String> availableRecipients;

    private View view;

    private Spinner select_recipient_spinner;

    private ArrayAdapter<String> select_recipient_spinnerAdapter;

    private WindowManager.LayoutParams lp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        availableRecipients = getArguments().getStringArrayList("availableRecipients");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.spinner_dialog, null);

        builder.setTitle(R.string.edit_recipient);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                            String recipientChosen = select_recipient_spinner.getSelectedItem().toString();

                            editRecipientDialogInterface.onEditRecipientDialogOkButtonClick(EditRecipientDialog.this, recipientChosen);
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

    public interface EditRecipientDialogInterface {

        void onEditRecipientDialogOkButtonClick(DialogFragment dialog, String new_recipient);
    }

    EditRecipientDialogInterface editRecipientDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            editRecipientDialogInterface = (EditRecipientDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        select_recipient_spinner = (Spinner) view.findViewById(R.id.select_item_spinner);

        select_recipient_spinnerAdapter = new ArrayAdapter<>(context, R.layout.my_custom_spinner, availableRecipients);

        select_recipient_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        select_recipient_spinner.setAdapter(select_recipient_spinnerAdapter);
    }
}
