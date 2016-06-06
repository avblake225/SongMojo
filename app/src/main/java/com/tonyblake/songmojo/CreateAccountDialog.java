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
import android.widget.Toast;

public class CreateAccountDialog extends DialogFragment {

    private Context context;
    private View view;
    private EditText et_firstName, et_lastName, et_username, et_password;
    private String firstName, lastName, username, password;
    private WindowManager.LayoutParams lp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.create_account_dialog, null);

        firstName = null;
        lastName = null;
        username = null;
        password = null;

        builder.setTitle(R.string.create_account)
                .setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if("".equals(username)){

                            showToastMessage(context.getString(R.string.no_username_entered));
                        }
                        if("".equals(password)){

                            showToastMessage(context.getString(R.string.no_password_entered));
                        }
                        else{

                            firstName = et_firstName.getText().toString();

                            lastName = et_lastName.getText().toString();

                            username = et_username.getText().toString();

                            password = et_password.getText().toString();

                            createAccountDialogInterface.onCreateAccountDialogSearchClick(CreateAccountDialog.this, firstName, lastName, username, password);
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
                Button searchButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                cancelButton.setTextColor(Color.BLACK);
                searchButton.setTextColor(Color.BLACK);

                final Drawable cancelButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cancelButton.setBackground(cancelButtonBackground);
                }

                final Drawable searchButtonBackground = getResources().getDrawable(R.drawable.background_color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    searchButton.setBackground(searchButtonBackground);
                }
            }
        });

        return dialog;
    }

    public interface CreateAccountDialogInterface {

        void onCreateAccountDialogSearchClick(DialogFragment dialog, String firstName, String lastName, String username, String password);
    }

    CreateAccountDialogInterface createAccountDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            createAccountDialogInterface = (CreateAccountDialogInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        et_firstName = (EditText) view.findViewById(R.id.et_firstName);

        et_lastName = (EditText) view.findViewById(R.id.et_lastName);

        et_username = (EditText) view.findViewById(R.id.et_username);

        et_password = (EditText) view.findViewById(R.id.et_password);
    }

    private void showToastMessage(CharSequence text) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
