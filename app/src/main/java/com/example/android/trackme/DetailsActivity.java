package com.example.android.trackme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;
import com.example.android.trackme.model.UserDetails;
import com.example.android.trackme.utils.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {

    private TextView mName,mEmail,mDepartment,mDesignation;
    private DatabaseReference databaseReference;
    private SQLiteDatabase mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RegisterDbHelper dbHelper=new RegisterDbHelper(this);
        mDb=dbHelper.getReadableDatabase();
        String[] projection = {
                RegisterContract.RegisterEntry._ID,
                RegisterContract.RegisterEntry.COLUMN_PUI,
                RegisterContract.RegisterEntry.COLUMN_NAME,
                RegisterContract.RegisterEntry.COLUMN_EMAIL,
                RegisterContract.RegisterEntry.COLUMN_DEPARTMENT,
                RegisterContract.RegisterEntry.COLUMN_DESIGNATION
        };

        Cursor cursor = mDb.query(
                RegisterContract.RegisterEntry.TABLE_NAME,                     // The table to query
                projection,                             // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        cursor.moveToFirst();

        mName=(TextView) findViewById(R.id.value_name);
        mEmail=(TextView) findViewById(R.id.value_email);
        mDepartment=(TextView) findViewById(R.id.value_department);
        mDesignation=(TextView) findViewById(R.id.value_designation);

        mName.setText(cursor.getString(2));
        mEmail.setText(cursor.getString(3));
        mDepartment.setText(cursor.getString(4));
        mDesignation.setText(cursor.getString(5));








    }
}