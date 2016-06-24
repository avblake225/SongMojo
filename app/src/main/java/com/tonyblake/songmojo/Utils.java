package com.tonyblake.songmojo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Utils {

    public static boolean connectedToNetwork(Context context){

        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null){

            NetworkInfo ni = cm.getActiveNetworkInfo();

            if(ni != null){

                connected = ni.isConnected();
            }
        }

        return connected;
    }

    public static void showToastMessage(Context context, CharSequence text) {

        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
