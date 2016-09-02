package com.tonyblake.songmojo;

import android.content.Context;

public class NewFileReceivedManager {

    private Context context;

    private DBManager dbManager;

    public NewFileReceivedManager(Context context){

        this.context = context;

        dbManager = new DBManager(context);
    }

    public boolean addToDatabase(String filename){

        boolean result = dbManager.insertDataIntoNewFileReceivedTable(filename);

        return result;
    }
}
