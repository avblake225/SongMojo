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

    // Files Downloaded Table
    public static final String FILES_DOWNLOADED_TABLE = "files_downloaded_table";
    public static final String FILES_DOWNLOADED_TABLE_COL_1 = "ID";
    public static final String FILES_DOWNLOADED_TABLE_COL_2 = "SENDER";
    public static final String FILES_DOWNLOADED_TABLE_COL_3 = "FILE_NAME";
    public static final String FILES_DOWNLOADED_TABLE_COL_4 = "DURATION";
    public static final String FILES_DOWNLOADED_TABLE_COL_5 = "FILE_TYPE";
    public static final String FILES_DOWNLOADED_TABLE_COL_6 = "DATE";

    // Recent Activity Table
    private final String RECENT_ACTIVITY_TABLE = "recent_activity_table";
    private final String RECENT_ACTIVITY_TABLE_COL_1 = "ID";
    private final String RECENT_ACTIVITY_TABLE_COL_2 = "USER";
    private final String RECENT_ACTIVITY_TABLE_COL_3 = "DATE";
    private final String RECENT_ACTIVITY_TABLE_COL_4 = "TIME";
    private final String RECENT_ACTIVITY_TABLE_COL_5 = "ACTION";

    // Band Members Table
    public static final String BAND_MEMBERS_TABLE = "band_members_table";
    public static final String BAND_MEMBERS_TABLE_COL_1 = "ID";
    public static final String BAND_MEMBERS_TABLE_COL_2 = "USER";
    public static final String BAND_MEMBERS_TABLE_COL_3 = "FULLNAME";

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
        db.execSQL("CREATE TABLE " + FILES_DOWNLOADED_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, SENDER TEXT, FILE_NAME TEXT, DURATION TEXT, FILE_TYPE TEXT, DATE TEXT)");
        db.execSQL("CREATE TABLE " + RECENT_ACTIVITY_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER TEXT, DATE TEXT, TIME TEXT, ACTION TEXT)");
        db.execSQL("CREATE TABLE " + BAND_MEMBERS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER TEXT, FULLNAME TEXT)");
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

    public boolean insertDataIntoFilesDownloadedTable(String sender, String file_name, String duration, String file_type, String date){

        ContentValues contentValues = new ContentValues();

        contentValues.put(FILES_DOWNLOADED_TABLE_COL_2, sender);
        contentValues.put(FILES_DOWNLOADED_TABLE_COL_3, file_name);
        contentValues.put(FILES_DOWNLOADED_TABLE_COL_4, duration);
        contentValues.put(FILES_DOWNLOADED_TABLE_COL_5, file_type);
        contentValues.put(FILES_DOWNLOADED_TABLE_COL_6, date);

        long result = db.insert(FILES_DOWNLOADED_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean insertDataIntoRecentActivityTable(String user, String date, String time, String action){

        ContentValues contentValues = new ContentValues();

        contentValues.put(RECENT_ACTIVITY_TABLE_COL_2, user);
        contentValues.put(RECENT_ACTIVITY_TABLE_COL_3, date);
        contentValues.put(RECENT_ACTIVITY_TABLE_COL_4, time);
        contentValues.put(RECENT_ACTIVITY_TABLE_COL_5, action);

        long result = db.insert(RECENT_ACTIVITY_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean insertDataIntoBandMembersTable(String user, String fullname){

        ContentValues contentValues = new ContentValues();

        contentValues.put(BAND_MEMBERS_TABLE_COL_2, user);
        contentValues.put(BAND_MEMBERS_TABLE_COL_3, fullname);

        long result = db.insert(BAND_MEMBERS_TABLE, null, contentValues);

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
        db.execSQL("DROP TABLE IF EXISTS" + FILES_DOWNLOADED_TABLE);
        db.execSQL("DROP TABLE IF EXISTS" + RECENT_ACTIVITY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS" + BAND_MEMBERS_TABLE);
        onCreate(db);
    }

    public Cursor rawQuery(String query){

        Cursor res = db.rawQuery(query,null);
        return res;
    }

    public void deleteBandMember(String user, String bandMember){

        String query = "DELETE FROM " + BAND_MEMBERS_TABLE + " WHERE USER = '" + user + "' AND FULLNAME = '" + bandMember + "';";

        db.execSQL(query);
    }

    public void deleteSentFiles(){

        db.delete(FILES_SENT_TABLE,null,null);
    }

    public void deleteDownloadedFiles(){

        db.delete(FILES_DOWNLOADED_TABLE,null,null);
    }

    public String FILES_SENT_TABLE(){

        return FILES_SENT_TABLE;
    }

    public String FILES_DOWNLOADED_TABLE(){

        return FILES_DOWNLOADED_TABLE;
    }

    public String RECENT_ACTIVITY_TABLE(){

        return RECENT_ACTIVITY_TABLE;
    }

    public String BAND_MEMBERS_TABLE(){

        return BAND_MEMBERS_TABLE;
    }
}
