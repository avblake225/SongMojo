package com.tonyblake.songmojo;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        TimeZone gmtTime = TimeZone.getTimeZone("GMT+1");

        simpleDateFormat.setTimeZone(gmtTime);

        String currentDateandTime = simpleDateFormat.format(new Date());

        return currentDateandTime;
    }

    public static ArrayList<String> getBandMembers(DBManager dbManager, Context context) {

        ArrayList<String> bandMembers = new ArrayList<>();

        String query = context.getString(R.string.select_all_rows_from) + " " + dbManager.BAND_MEMBERS_TABLE() + ";";

        Cursor cursor;

        try {

            cursor = dbManager.rawQuery(query);

            cursor.moveToFirst();

            do {

                String name = cursor.getString(1);

                bandMembers.add(name);
            }
            while (cursor.moveToNext());
        }
        catch (Exception e) {

        }

        return bandMembers;
    }

    public static String getDeviceToken(){

        return FirebaseInstanceId.getInstance().getToken();
    }

    public static String[] separateWords(String fullname){

        // Convert fullname string to character array
        char[] fullnamechars = new char[fullname.length()];

        for(int i=0;i<fullname.length();i++){

            fullnamechars[i] = fullname.charAt(i);
        }

        String[] names = new String[2];

        char[] firstname, lastname;

        // Search for whitespace
        for(int i=0;i<fullname.length()-1;i++){

            if(fullnamechars[i] == ' '){

                // Get first name
                firstname = new char[i];

                for(int j=0;j<i;j++){

                    firstname[j] = fullnamechars[j];
                }

                names[0] = String.valueOf(firstname);

                // Get last name
                int charsRemaining = (fullname.length()-1)-i;

                lastname = new char[charsRemaining];

                int k=i+1;

                int l=0;

                while(l<charsRemaining){

                    lastname[l] = fullnamechars[k];

                    k++;
                    l++;
                }

                names[1] = String.valueOf(lastname);

                break;
            }
        }

        return names;
    }

    public static String[] separateDateAndTime(String dateAndTime){

        String[] parts = new String[dateAndTime.length()];

        int numWhiteSpaces = 0;

        for(int i=0;i<dateAndTime.length()-1;i++){

            if(dateAndTime.charAt(i) == ' '){

                numWhiteSpaces++;

                if(numWhiteSpaces == 3){

                    parts[0] = dateAndTime.substring(0,i);

                    parts[1] = dateAndTime.substring(i, dateAndTime.length());

                    break;
                }
            }
        }

        return parts;
    }
}
