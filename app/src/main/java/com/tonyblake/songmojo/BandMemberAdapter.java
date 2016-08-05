package com.tonyblake.songmojo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BandMemberAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    String name = null;
    int i=0;

    public BandMemberAdapter(Activity a, ArrayList d,Resources resLocal) {

        activity = a;
        data=d;
        res = resLocal;

        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {

        if(data.size()<=0) return 1;

        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {

    }

    public static class ViewHolder{

        public TextView band_member_name;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if(convertView==null){

            v = inflater.inflate(R.layout.band_member_item_layout, null);

            holder = new ViewHolder();
            holder.band_member_name = (TextView) v.findViewById(R.id.tv_band_member_name);

            v.setTag( holder );
        }
        else {

            holder = (ViewHolder) v.getTag();
        }

        if(data.size() != 0){

            name = (String)data.get( position );

            holder.band_member_name.setText(name);

            v.setOnClickListener(new OnItemClickListener( position ));
        }

        return v;
    }

    private class OnItemClickListener implements View.OnClickListener {

        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            EditBandMembers editBandMembers = (EditBandMembers)activity;

            editBandMembers.onItemClick(mPosition);
        }
    }

    public int clearData(int position) {

        data.remove(position);

        return data.size();
    }
}