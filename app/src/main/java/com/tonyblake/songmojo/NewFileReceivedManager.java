package com.tonyblake.songmojo;

import android.content.Context;

public class NewFileReceivedManager {

    private Context context;

    private DBManager dbManager;

    public NewFileReceivedManager(Context context){

        this.context = context;

        dbManager = new DBManager(context);
    }

    public boolean addToDatabase(FileReceived file){

        boolean result = dbManager.insertDataIntoFilesReceivedTable(file.sender,file.filename,
                                                file.duration,file.filetype,file.dateAndTime);

        return result;
    }
}
