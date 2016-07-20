package com.tonyblake.songmojo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DBManager extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "songmojodatabase.db";

    // Files Sent Table
    public static final String FILES_SENT_TABLE = "files_sent_table";
    public static final String FILES_SENT_TABLE_COL_1 = "ID";
    public static final String FILES_SENT_TABLE_COL_2 = "RECIPIENT";
    public static final String FILES_SENT_TABLE_COL_3 = "FILE_NAME";
    public static final String FILES_SENT_TABLE_COL_4 = "DURATION";
    public static final String FILES_SENT_TABLE_COL_5 = "FILE_TYPE";
    public static final String FILES_SENT_TABLE_COL_6 = "DATE";

    // Band Members Table
    public static final String BAND_MEMBERS_TABLE = "band_members_table";
    public static final String BAND_MEMBERS_TABLE_COL_1 = "ID";
    public static final String BAND_MEMBERS_TABLE_COL_2 = "FULLNAME";

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

        db.execSQL("CREATE TABLE " + FILES_SENT_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPIENT TEXT, FILE_NAME TEXT, DURATION TEXT, FILE_TYPE TEXT, DATE TEXT)");
        db.execSQL("CREATE TABLE " + BAND_MEMBERS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FULLNAME TEXT)");
    }

    public boolean insertDataIntoFilesSentTable(String recipient, String file_name, String duration, String file_type, String date){

        ContentValues contentValues = new ContentValues();

        contentValues.put(FILES_SENT_TABLE_COL_2, recipient);
        contentValues.put(FILES_SENT_TABLE_COL_3, file_name);
        contentValues.put(FILES_SENT_TABLE_COL_4, duration);
        contentValues.put(FILES_SENT_TABLE_COL_5, file_type);
        contentValues.put(FILES_SENT_TABLE_COL_6, date);

        long result = db.insert(FILES_SENT_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean insertDataIntoBandMembersTable(String fullname){

        ContentValues contentValues = new ContentValues();

        contentValues.put(BAND_MEMBERS_TABLE_COL_2, fullname);

        long result = db.insert(FILES_SENT_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + FILES_SENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS" + BAND_MEMBERS_TABLE);
        onCreate(db);

    }

    public Cursor rawQuery(String query){

        Cursor res = db.rawQuery(query,null);
        return res;
    }

    public void deleteSentFiles(){

        db.delete(FILES_SENT_TABLE,null,null);
    }

    public String FILES_SENT_TABLE(){

        return FILES_SENT_TABLE;
    }

    public String BAND_MEMBERS_TABLE(){

        return BAND_MEMBERS_TABLE;
    }
}
