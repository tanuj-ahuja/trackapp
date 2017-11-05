package com.example.android.trackme.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;
import com.example.android.trackme.utils.JsonUtils;
import com.example.android.trackme.utils.NetworkUtils;
import com.example.android.trackme.utils.NotificationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;

/**
 * Created by tanujanuj on 20/10/17.
 */

public class ReminderTasks {

    private static String mID;
    private static Long mPC;
    private  static SQLiteDatabase mDb;



    public static final String ACTION_MEETING_REMINDER="meeting-reminder";
    public static void executeTask(Context context, String action){
        if(ACTION_MEETING_REMINDER.equals(action)) {
            issueMeetingRemider(context);

        }
    }

    private  static void issueMeetingRemider(final Context context) {

        RegisterDbHelper dbHelper=new RegisterDbHelper(context);
        mDb=dbHelper.getWritableDatabase();
        Cursor cursor;
        String[] projection = {
                RegisterContract.RegisterEntry._ID,
                RegisterContract.RegisterEntry.COLUMN_PUI,
                RegisterContract.RegisterEntry.COLUMN_MEETING_COUNT
        };

        cursor = mDb.query(
                RegisterContract.RegisterEntry.TABLE_NAME,                     // The table to query
                projection,                             // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        cursor.moveToFirst();
        mID=cursor.getString(1);
        String mC=String.valueOf(cursor.getString(2));




        /*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference meetingRef=databaseReference.child("users").child(String.valueOf(mID)).child("meetingcount");
        meetingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPC= (Long) dataSnapshot.getValue();
                if(mPC==3){
                    NotificationUtils.remindUser(context);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        URL url1= NetworkUtils.buildUrlForDetails(mID);
        URL url2=NetworkUtils.buildUrlForMeetingDetails(mID);
        String meetingResults="q";
        String meetingDetails="w";
        String activity[]=new String[5];
        activity[0]="e";
        try {
             meetingResults=NetworkUtils.getResponseFromHttpUrl(url1);
             meetingDetails=NetworkUtils.getResponseFromHttpUrl(url2);
             activity= JsonUtils.getSimpleDetailStringsFromJson(context,meetingDetails,meetingResults);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!meetingResults.equals(mC)&&!mC.equals(null)&&!activity[0].equals("e")) {
            ContentValues contentValues=new ContentValues();
            contentValues.put(RegisterContract.RegisterEntry.COLUMN_MEETING_COUNT,meetingResults);
            mDb.update(RegisterContract.RegisterEntry.TABLE_NAME,contentValues,null,null);
            NotificationUtils.remindUser(context, activity[1]+"-"+activity[0],mID);
        }

        cursor = mDb.query(
                RegisterContract.RegisterEntry.TABLE_NAME,                     // The table to query
                projection,                             // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        cursor.moveToFirst();
        String d=cursor.getString(2);



    }
}
