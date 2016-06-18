package com.tonyblake.songmojo;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SentFileListItem {

    private LinearLayout files_sent_list;

    private View sent_file_item_layout;

    private TextView tv_recipient;

    private TextView tv_file_name;

    private TextView tv_date;

    public SentFileListItem(LinearLayout files_sent_list, View sent_file_item_layout){

        this.sent_file_item_layout = sent_file_item_layout;

        this.files_sent_list = files_sent_list;

        tv_recipient = (TextView)sent_file_item_layout.findViewById(R.id.tv_recipient);

        tv_file_name = (TextView)sent_file_item_layout.findViewById(R.id.tv_file_name);

        tv_date = (TextView)sent_file_item_layout.findViewById(R.id.tv_date);
    }

    public void setRecipient(String recipient){

        tv_recipient.setText(recipient);
    }

    public void setFileName(String filename){

        tv_file_name.setText(filename);
    }

    public void setDate(String date){

        tv_date.setText(date);
    }

    public void finish(){

        files_sent_list.addView(sent_file_item_layout);
    }
}
