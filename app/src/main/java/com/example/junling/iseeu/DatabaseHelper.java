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
import java.util.Locale;

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
    private static final String KEY_ID = "id";

    //Caller column names
    private static final String CALLER_Number = "tabletNum";
    private static final String CALLER_Nane = "callerName";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create Tablet statements
    private static final String CREATE_TABLE_TABLET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TABLET + "(" + KEY_ID + " TEXT PRIMARY KEY," + TABLET_Number
            + " TEXT," + TABLET_PNAME + " TEXT)";

    //Create Caller statement
    private static final String CREATE_CALLER_TABLET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CALLER + "(" + KEY_ID + " TEXT PRIMARY KEY," + CALLER_Number
            + " TEXT," + CALLER_Nane + " TEXT)";

    //Drop Tablet Table
    private static final String DROP_TABLE_TABLET = "DROP TABLE " + TABLE_TABLET;

    //Drop Caller Table
    private static final String DROP_CALLER_TABLET = "DROP TABLE " + TABLE_CALLER;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required
        db.execSQL(CREATE_TABLE_TABLET);
        db.execSQL(CREATE_CALLER_TABLET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLER);
        db.execSQL(CREATE_CALLER_TABLET);
    }

    //drop Tablet table
    public void dropTablet(SQLiteDatabase db){
        db.execSQL(DROP_TABLE_TABLET);
        db.execSQL(DROP_CALLER_TABLET);
    }

    public boolean hasTablet(String deviceNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TABLET + " WHERE tabletNum = '" + deviceNumber + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if(c != null){
            return true;
        }else
            return false;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //helpful DB tutorial https://developer.android.com/training/basics/data-storage/databases.html#DefineContract
}
