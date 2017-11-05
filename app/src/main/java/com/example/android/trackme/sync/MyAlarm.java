package com.example.android.trackme.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.Settings;

import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;

/**
 * Created by tanujanuj on 28/10/17.
 */

public class MyAlarm extends BroadcastReceiver {

    private AsyncTask mBackgroundTask;
    private SQLiteDatabase mDb;
    Cursor mCursor;
    String mID;
    @Override
    public void onReceive(final Context context, Intent intent) {

        RegisterDbHelper dbHelper = new RegisterDbHelper(context);
        mDb = dbHelper.getReadableDatabase();

        String[] projection = {
                RegisterContract.RegisterEntry._ID,
                RegisterContract.RegisterEntry.COLUMN_PUI
        };

        mCursor = mDb.query(
                RegisterContract.RegisterEntry.TABLE_NAME,                     // The table to query
                projection,                             // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        mCursor.moveToFirst();

        mID = mCursor.getString(1);


        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_MEETING_REMINDER);

                return null;
            }

        };
        mBackgroundTask.execute();
    }
}
