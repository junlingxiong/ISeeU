package com.example.junling.iseeu;

/**
 * Created by Junling on 19/8/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.*;

import com.example.junling.iseeu.entities.Tablet;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ISeeUDB";

    // Table Names
    private static final String TABLE_TABLET = "tablet";
    private static final String TABLE_CALLER = "caller";

    //Tablet Column names
    private static final String TABLET_Number = "tabletNum";
    private static final String TABLET_PNAME = "patientName";
    private static final String TABLET_PASSWORD = "password";
    private static final String KEY_ID = "id";

    //Caller column names
    private static final String CALLER_Number = "tabletNum";
    private static final String CALLER_Name = "callerName";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create Tablet statements
    private static final String CREATE_TABLE_TABLET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TABLET + "(" + KEY_ID + " TEXT PRIMARY KEY," + TABLET_Number
            + " TEXT," + TABLET_PNAME
            + " TEXT," + TABLET_PASSWORD + " TEXT)";

    //Create Caller statement
    private static final String CREATE_CALLER_TABLET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CALLER + "(" + KEY_ID + " TEXT PRIMARY KEY," + CALLER_Number
            + " TEXT," + CALLER_Name + " TEXT)";

    //Drop Tablet Table
    private static final String DROP_TABLE_TABLET = "DROP TABLE " + TABLE_TABLET;

    //Drop Caller Table
    private static final String DROP_CALLER_TABLET = "DROP TABLE " + TABLE_CALLER;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required
        db.execSQL(CREATE_TABLE_TABLET);
        db.execSQL(CREATE_CALLER_TABLET);
        //create default tablet

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        ContentValues values3 = new ContentValues();

        values1.put(KEY_ID, 12344444);
        values1.put(TABLET_Number, "TB110");
        values1.put(TABLET_PNAME, "FSM");
        values1.put(TABLET_PASSWORD,"123");
        // insert row
        long todo_id1 = db.insert(TABLE_TABLET, null, values1);

        values2.put(KEY_ID, 12355555);
        values2.put(TABLET_Number, "TB120");
        values2.put(TABLET_PNAME, "LJL");
        values2.put(TABLET_PASSWORD,"123");
        // insert row
        long todo_id2 = db.insert(TABLE_TABLET, null, values2);

        values3.put(KEY_ID, 123555665);
        values3.put(TABLET_Number, "TB130");
        values3.put(TABLET_PNAME, "XZ");
        values3.put(TABLET_PASSWORD,"123");
        // insert row
        long todo_id3 = db.insert(TABLE_TABLET, null, values3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL(DROP_CALLER_TABLET);
        db.execSQL(CREATE_CALLER_TABLET);
    }

    //drop Tablet table
    public void dropTablet(SQLiteDatabase db){
        db.execSQL(DROP_TABLE_TABLET);
        db.execSQL(DROP_CALLER_TABLET);
    }

    public boolean hasTablet(String deviceNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TABLET + " WHERE TABLETNUM = '" + deviceNumber + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null && c.getCount()>0){
            return true;
        }else
            return false;
    }

    public boolean checkPass(String deviceNum, String pass){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TABLET + " WHERE TABLETNUM = '" + deviceNum + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        Tablet tablet = new Tablet();

        tablet.setId(c.getString(c.getColumnIndex(KEY_ID)));
        tablet.setTabletNum(c.getString(c.getColumnIndex(TABLET_Number)));
        tablet.setPassword(c.getString(c.getColumnIndex(TABLET_PASSWORD)));
        tablet.setPatientName(c.getString(c.getColumnIndex(TABLET_PNAME)));

        if(tablet.getPassword().equals(pass))
            return true;
        else
            return false;
    }

    //create caller
    public void createCaller(String deviceNum, String caller){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        String uniqueID = UUID.randomUUID().toString();

        values.put(KEY_ID, uniqueID);
        values.put(CALLER_Number, deviceNum);
        values.put(CALLER_Name, caller);
        // insert row
        long todo_id1 = db.insert(TABLE_CALLER, null, values);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //helpful DB tutorial https://developer.android.com/training/basics/data-storage/databases.html#DefineContract
}
