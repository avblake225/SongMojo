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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;

public class FindBandMemberDialog extends DialogFragment {

    private Context context;

    private WindowManager.LayoutParams lp;

    private LayoutInflater inflator;

    private View layout;

    private AutoCompleteTextView tv_name_entered;

    private String fullname;

    private ArrayList<String> fullnames;

    private String user;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout = inflator.inflate(R.layout.find_band_member_dialog, null);

        fullnames = new ArrayList<>();

        user = getArguments().getString("user");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(layout)
                .setTitle(context.getString(R.string.find_band_member))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if(fullname == null) {

                            fullname = tv_name_entered.getText().toString();
                        }

                        findBandMemberDialogInterface.onFindBandMemberDialogOkButtonClick(FindBandMemberDialog.this, fullname);

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

    public interface FindBandMemberDialogInterface{

        void onFindBandMemberDialogOkButtonClick(DialogFragment dialog, String name);
    }

    FindBandMemberDialogInterface findBandMemberDialogInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            findBandMemberDialogInterface = (FindBandMemberDialogInterface)activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tv_name_entered = (AutoCompleteTextView) layout.findViewById(R.id.tv_name_entered);

        for(User u: Login.users){

            if(!u.fullName.equals(user)){

                fullnames.add(u.fullName);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, fullnames);

        tv_name_entered.setAdapter(adapter);

        tv_name_entered.setThreshold(1);
    }

    @Override
    public void onResume() {
        super.onResume();

        tv_name_entered.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                fullname = (String) parent.getItemAtPosition(position);
            }
        });
    }
}
