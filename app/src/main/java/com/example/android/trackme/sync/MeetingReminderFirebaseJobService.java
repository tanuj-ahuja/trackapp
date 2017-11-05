package com.example.android.trackme.sync;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by tanujanuj on 20/10/17.
 */

public class MeetingReminderFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;
    private SQLiteDatabase mDb;
    Cursor mCursor;
    String mID;


    @Override
    public boolean onStartJob(final JobParameters job){
        RegisterDbHelper dbHelper=new RegisterDbHelper(this);
        mDb=dbHelper.getReadableDatabase();

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

        mID=mCursor.getString(1);


        mBackgroundTask =new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Context context=MeetingReminderFirebaseJobService.this;
                ReminderTasks.executeTask(context,ReminderTasks.ACTION_MEETING_REMINDER);

                return null;
            }
            @Override
            protected void onPostExecute(Object o){
               jobFinished(job,false);
            }
        };
        if (mID!=null) {
            mBackgroundTask.execute();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mBackgroundTask!=null){
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
