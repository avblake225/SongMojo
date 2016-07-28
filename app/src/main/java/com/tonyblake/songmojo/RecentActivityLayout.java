package com.tonyblake.songmojo;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecentActivityLayout{

    private View recent_activity_layout;

    private LinearLayout recent_activity_layout_container;

    private TextView tv_time, tv_action;

    public RecentActivityLayout(View recent_activity_layout, LinearLayout recent_activity_layout_container){

        this.recent_activity_layout = recent_activity_layout;

        this.recent_activity_layout_container = recent_activity_layout_container;

        tv_time = (TextView) recent_activity_layout.findViewById(R.id.tv_time);

        tv_action = (TextView) recent_activity_layout.findViewById(R.id.tv_action);
    }

    public void setTime(String time){

        tv_time.setText(time);
    }

    public void setAction(String action){

        tv_action.setText(action);
    }

    public void finish(){

        recent_activity_layout_container.addView(recent_activity_layout);
    }
}
