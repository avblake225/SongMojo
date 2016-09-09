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
}
