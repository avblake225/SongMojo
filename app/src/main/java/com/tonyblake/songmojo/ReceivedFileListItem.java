package com.tonyblake.songmojo;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReceivedFileListItem {

    private LinearLayout files_sent_or_received_list;

    private View received_file_item_layout;

    private TextView tv_sender, tv_file_name, tv_date, tv_file_type, tv_duration;

    public ReceivedFileListItem(LinearLayout files_received_list, View received_file_item_layout){

        this.received_file_item_layout = received_file_item_layout;

        this.files_sent_or_received_list = files_received_list;

        tv_sender = (TextView)received_file_item_layout.findViewById(R.id.tv_sender);
        tv_file_name = (TextView)received_file_item_layout.findViewById(R.id.tv_file_name);
        tv_date = (TextView)received_file_item_layout.findViewById(R.id.tv_date);
        tv_file_type = (TextView)received_file_item_layout.findViewById(R.id.tv_file_type);
        tv_duration = (TextView)received_file_item_layout.findViewById(R.id.tv_duration);
    }

    public void setSender(String sender){

        tv_sender.setText(sender);
    }

    public void setFileName(String filename){

        tv_file_name.setText(filename);
    }

    public void setDate(String date){

        tv_date.setText(date);
    }

    public void setFileType(String filetype){

        tv_file_type.setText(filetype);
    }

    public void setDuration(String duration){

        tv_duration.setText(duration);
    }

    public void finish(){

        files_sent_or_received_list.addView(received_file_item_layout);
    }
}
