package com.tonyblake.songmojo;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static int cameraId;

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

    public static Camera getCameraInstance(){
        // TODO Auto-generated method stub
        Camera c = null;
        try {

            cameraId = findFrontFacingCamera();

            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static String formatInterval(final long millis) {

        final long hr = TimeUnit.MILLISECONDS.toHours(millis);
        final long min = TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(millis - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));

        return String.format("%02d:%02d", min, sec);
    }

    public static String removePrefix(String filename){

        char[] chars = new char[filename.length()-4];

        for(int i=0;i<filename.length();i++){

            char letter = filename.charAt(i);

            String letter_str = String.valueOf(letter);

            if(letter_str.equals(".")){

                break;
            }
            else{

                chars[i] = letter;
            }
        }

        String filenameWithoutPrefix = new String(chars);

        return filenameWithoutPrefix;
    }

    public static ArrayList<String> getBandMembers(Context context, DBManager dbManager){

        ArrayList<String> bandMembers = new ArrayList<>();

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.BAND_MEMBERS_TABLE() + ";";

        Cursor cursor;

        try{

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do{

                String bandMember = cursor.getString(1);

                bandMembers.add(bandMember);
            }
            while(cursor.moveToNext());
        }
        catch(Exception e){

            Log.e("getBandMembersException", "Problem reading band members from DB");
        }

        return bandMembers;
    }

    public static String getCurrentDate(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");

        String currentDate = simpleDateFormat.format(new Date());

        return currentDate;
    }

    public static String getCurrentTime(){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String currentTime = sdf.format(new Date());

        return currentTime;
    }

    public static String getCurrentDateAndTime(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        TimeZone gmtTime = TimeZone.getTimeZone("GMT");

        simpleDateFormat.setTimeZone(gmtTime);

        String currentDateandTime = simpleDateFormat.format(new Date());

        return currentDateandTime;
    }
}
