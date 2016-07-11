package com.tonyblake.songmojo;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReceivedFileListItem {

    private LinearLayout files_sent_or_received_list;

    private View received_file_item_layout;

    private TextView tv_file_name;

    public ReceivedFileListItem(LinearLayout files_received_list, View received_file_item_layout){

        this.received_file_item_layout = received_file_item_layout;

        this.files_sent_or_received_list = files_received_list;

        tv_file_name = (TextView)received_file_item_layout.findViewById(R.id.tv_file_name);
    }

    public void setFileName(String filename){

        tv_file_name.setText(filename);
    }

    public void finish(){

        files_sent_or_received_list.addView(received_file_item_layout);
    }
}
