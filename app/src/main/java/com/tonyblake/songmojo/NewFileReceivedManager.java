package com.tonyblake.songmojo;

import android.content.Context;

public class NewFileReceivedManager {

    private DBManager dbManager;

    public NewFileReceivedManager(Context context){

        dbManager = new DBManager(context);
    }

    public boolean addToDatabase(FileReceived file){

        boolean result = dbManager.insertDataIntoFilesReceivedTable(file.sender,file.filename,
                                                file.duration,file.filetype,file.dateAndTime);

        return result;
    }

    public boolean addStatus(String filename, String status){

        boolean result = dbManager.insertDataIntoFileAvailableTable(filename, status);

        return result;
    }

    public boolean recordActivity(FileReceived file, String action){

        String[] parts = Utils.separateDateAndTime(file.dateAndTime);

        String date = parts[0];
        String time = parts[1];

        boolean result = dbManager.insertDataIntoRecentActivityTable(date,time,action);

        dbManager.close();

        return result;
    }
}
