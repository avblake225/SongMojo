package com.tonyblake.songmojo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DBManager extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "songmojodatabase.db";
    public static final String TABLE_NAME = "files_sent_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "RECIPIENT";
    public static final String COL_3 = "FILE NAME";
    public static final String COL_4 = "TIME_STAMP";

    private File db_file;

    public SQLiteDatabase db;

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, 1);

        db_file = context.getDatabasePath(DATABASE_NAME);

        if(db_file.exists()){

            db = SQLiteDatabase.openDatabase(db_file.toString(),null,SQLiteDatabase.OPEN_READWRITE);
        }
        else {

            db = this.getWritableDatabase();
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPIENT TEXT, FILE_NAME TEXT, TIME_STAMP TIMESTAMP)");
    }

    public boolean insertData(String recipient, String file_name, String time_stamp){

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, recipient);
        contentValues.put(COL_3, file_name);
        contentValues.put(COL_4, time_stamp);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);

    }

    public Cursor rawQuery(String query){

        Cursor res = db.rawQuery(query,null);
        return res;
    }

    public String getTableName(){

        return TABLE_NAME;
    }
}
