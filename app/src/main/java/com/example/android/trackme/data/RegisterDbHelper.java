package com.example.android.trackme.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by tanujanuj on 06/09/17.
 */

public class RegisterDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="track.db";
    private static final int DATABASE_VERSION=2;
    public RegisterDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_REGISTER_TABLE="CREATE TABLE " + RegisterContract.RegisterEntry.TABLE_NAME +"("+
                RegisterContract.RegisterEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                RegisterContract.RegisterEntry.COLUMN_NAME+" TEXT NOT NULL, "+
                RegisterContract.RegisterEntry.COLUMN_EMAIL+" TEXT NOT NULL, "+
                RegisterContract.RegisterEntry.COLUMN_DEPARTMENT+" TEXT NOT NULL, "+
                RegisterContract.RegisterEntry.COLUMN_DESIGNATION+" TEXT NOT NULL, "+
                RegisterContract.RegisterEntry.COLUMN_PUI+" TEXT NOT NULL, "+
                RegisterContract.RegisterEntry.COLUMN_MEETING_COUNT + " TEXT NOT NULL" +

                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_REGISTER_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+ RegisterContract.RegisterEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

