package com.tonyblake.songmojo;

import android.content.Context;

public class NewTokenManager {

    private Context context;

    private DBManager dbManager;

    public NewTokenManager(Context context){

        this.context = context;

        dbManager = new DBManager(context);
    }

    public boolean addToDatabase(String token){

        boolean result = dbManager.insertDataIntoDeviceTokenTable(token);

        return result;
    }
}
