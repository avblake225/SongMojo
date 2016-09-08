package com.tonyblake.songmojo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DBManager extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "songmojodatabase.db";

    // Files Sent Table
    private final String FILES_SENT_TABLE = "files_sent_table";
    private final String FILES_SENT_TABLE_COL_1 = "ID";
    private final String FILES_SENT_TABLE_COL_2 = "USER";
    private final String FILES_SENT_TABLE_COL_3 = "RECIPIENT";
    private final String FILES_SENT_TABLE_COL_4 = "FILE_NAME";
    private final String FILES_SENT_TABLE_COL_5 = "DURATION";
    private final String FILES_SENT_TABLE_COL_6 = "FILE_TYPE";
    private final String FILES_SENT_TABLE_COL_7 = "DATE";

    // Files Received Table
    private final String FILES_RECEIVED_TABLE = "files_received_table";
    private final String FILES_RECEIVED_TABLE_COL_1 = "ID";
    private final String FILES_RECEIVED_TABLE_COL_2 = "SENDER";
    private final String FILES_RECEIVED_TABLE_COL_3 = "FILE_NAME";
    private final String FILES_RECEIVED_TABLE_COL_4 = "DURATION";
    private final String FILES_RECEIVED_TABLE_COL_5 = "FILE_TYPE";
    private final String FILES_RECEIVED_TABLE_COL_6 = "DATE";

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

        db.execSQL("CREATE TABLE " + FILES_SENT_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER TEXT, RECIPIENT TEXT, FILE_NAME TEXT, DURATION TEXT, FILE_TYPE TEXT, DATE TEXT)");
        db.execSQL("CREATE TABLE " + FILES_RECEIVED_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, SENDER TEXT, FILE_NAME TEXT, DURATION TEXT, FILE_TYPE TEXT, DATE TEXT)");
        db.execSQL("CREATE TABLE " + RECENT_ACTIVITY_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER TEXT, DATE TEXT, TIME TEXT, ACTION TEXT)");
        db.execSQL("CREATE TABLE " + BAND_MEMBERS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FULLNAME TEXT)");
    }

    public boolean insertDataIntoFilesSentTable(String user, String recipient, String file_name, String duration, String file_type, String date){

        ContentValues contentValues = new ContentValues();

        contentValues.put(FILES_SENT_TABLE_COL_2, user);
        contentValues.put(FILES_SENT_TABLE_COL_3, recipient);
        contentValues.put(FILES_SENT_TABLE_COL_4, file_name);
        contentValues.put(FILES_SENT_TABLE_COL_5, duration);
        contentValues.put(FILES_SENT_TABLE_COL_6, file_type);
        contentValues.put(FILES_SENT_TABLE_COL_7, date);

        long result = db.insert(FILES_SENT_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean insertDataIntoFilesReceivedTable(String sender, String file_name, String duration, String file_type, String date){

        ContentValues contentValues = new ContentValues();

        contentValues.put(FILES_RECEIVED_TABLE_COL_2, sender);
        contentValues.put(FILES_RECEIVED_TABLE_COL_3, file_name);
        contentValues.put(FILES_RECEIVED_TABLE_COL_4, duration);
        contentValues.put(FILES_RECEIVED_TABLE_COL_5, file_type);
        contentValues.put(FILES_RECEIVED_TABLE_COL_6, date);

        long result = db.insert(FILES_RECEIVED_TABLE, null, contentValues);

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

    public boolean insertDataIntoBandMembersTable(String fullname){

        ContentValues contentValues = new ContentValues();

        contentValues.put(BAND_MEMBERS_TABLE_COL_2, fullname);

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
        db.execSQL("DROP TABLE IF EXISTS" + FILES_RECEIVED_TABLE);
        db.execSQL("DROP TABLE IF EXISTS" + RECENT_ACTIVITY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS" + BAND_MEMBERS_TABLE);
        onCreate(db);
    }

    public Cursor rawQuery(String query){

        Cursor res = db.rawQuery(query,null);
        return res;
    }

    public void deleteBandMember(String bandMember){

        String query = "DELETE FROM " + BAND_MEMBERS_TABLE + " WHERE FULLNAME = '" + bandMember + "';";

        db.execSQL(query);
    }

    public int getRowsInRecentActivityTable(Context context){

        String query = context.getString(R.string.select_all_rows_from) + " " + RECENT_ACTIVITY_TABLE + ";";

        Cursor cursor = null;

        try{
            cursor = db.rawQuery(query, null);
        }
        catch(Exception e){

            Log.e("dbReadError: ", "Error reading rows in Recent Activity table");
        }

        int numRows = 0;

        if(cursor != null){

           numRows = cursor.getCount();
        }

        return numRows;
    }

    public void deleteOldActivity(String date){

        String delete_query = "DELETE FROM " + RECENT_ACTIVITY_TABLE + " WHERE DATE != '" + date + "';";

        db.execSQL(delete_query);
    }

    public void deleteSentFiles(){

        db.delete(FILES_SENT_TABLE,null,null);
    }

    public void deleteReceivedFiles(){

        db.delete(FILES_RECEIVED_TABLE,null,null);
    }

    public String FILES_SENT_TABLE(){

        return FILES_SENT_TABLE;
    }

    public String FILES_RECEIVED_TABLE(){

        return FILES_RECEIVED_TABLE;
    }

    public String RECENT_ACTIVITY_TABLE(){

        return RECENT_ACTIVITY_TABLE;
    }

    public String BAND_MEMBERS_TABLE(){

        return BAND_MEMBERS_TABLE;
    }
}
