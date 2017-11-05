package com.example.android.trackme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;
import com.example.android.trackme.model.LatLng;
import com.example.android.trackme.model.StatusReport;
import com.example.android.trackme.model.UserDetails;
import com.example.android.trackme.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    EditText mName,mMobile,mEmail,mDepartment,mDesignation;
    Button mRegisterButton;
    private DatabaseReference databaseReference,mCountRef,mFlagPic;
    private SQLiteDatabase mDb;
    private Long mCount;
    Cursor mCursor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        if(mCursor.getCount()==1){
            Intent intent =new Intent(this,MainActivity.class);
            intent.putExtra("pui",mCursor.getString(1));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        mName=(EditText) findViewById(R.id.editText);
        mMobile=(EditText) findViewById(R.id.editText2);
        mEmail=(EditText) findViewById(R.id.editText4);
        mDepartment=(EditText) findViewById(R.id.editText5);
        mDesignation=(EditText) findViewById(R.id.editText6);
        mRegisterButton=(Button) findViewById(R.id.button2);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mCountRef=databaseReference.child("count");
        mCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCount= (Long) dataSnapshot.getValue();

                    Log.d("dvfdv",String.valueOf(mCount));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    mName.setError("Name can't be empty");
            }
        });

        mMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    mMobile.setError("Mobile number can't be empty");
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    mEmail.setError("Email can't be empty");
            }
        });

        mDepartment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    mDepartment.setError("Department name can't be empty");
            }
        });

        mDesignation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    mDesignation.setError("Designation can't be empty");
            }
        });


       dbHelper=new RegisterDbHelper(this);
        mDb=dbHelper.getWritableDatabase();



    }

    public void register(View view){
        String name=mName.getText().toString();
        String mobile=mMobile.getText().toString();
        String email=mEmail.getText().toString();
        String department=mDepartment.getText().toString();
        String designation=mDesignation.getText().toString();

        boolean n=validateName(name);
        boolean m=validateMobile(mobile);
        boolean e=validateEmail(email);
        boolean dp=validateDepartment(department);
        boolean ds=validateDesignation(designation);

        if(!n||!m||!e||!dp||!ds)
            return;

        else{

            DatabaseReference myref=databaseReference.child(Constants.FIREBASE_LOCATION).push();
            DatabaseReference idRef=databaseReference.child("pids");
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
            String userId=myref.getKey();
            LatLng latLng = new LatLng(28.708675,77.096611);
            UserDetails userDetails=new UserDetails(name,mobile,email,department,designation,latLng,0,0,0,0,timestampCreated,0);
            myref.setValue(userDetails);
            Map<String,Object> data=new HashMap<String, Object>();
            data.put(String.valueOf(mCount),userId);
            mCount++;
            idRef.updateChildren(data);
            long kh=addIdDb(userId,name,email,department,designation);
            mCountRef.setValue(mCount);

            /*
            DatabaseReference newRef=myRef.push();

            HashMap<String, Object> timestampCreated = new HashMap<>();
                       timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
            String userId=newRef.getKey();
            LatLng latLng = new LatLng(28.708675,77.096611);
            UserDetails userDetails=new UserDetails(name,mobile,email,department,designation,latLng,timestampCreated);
            newRef.setValue(userDetails);


*/
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
            Intent intent=new Intent(this,MainActivity.class);

            intent.putExtra("pui",mCursor.getString(1));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private boolean validateName(String name) {
        if(name.length()==0) {
            mName.setError("Enter name");
            return false;
        }
        else{
            return true;
        }
    }
    private boolean validateMobile(String mobile) {
        if(mobile.length()==0) {
            mMobile.setError("Enter mobile number");
            return false;
        }
        else{
            return true;
        }
    }
    private boolean validateEmail(String email) {
        if(email.length()==0) {
            mEmail.setError("Enter email");
            return false;
        }
        else{
            return true;
        }
    }
    private boolean validateDepartment(String department) {
        if(department.length()==0) {
            mDepartment.setError("Enter department");
            return false;
        }
        else{
            return true;
        }
    }
    private boolean validateDesignation(String designation) {
        if(designation.length()==0) {
            mDesignation.setError("Enter designation");
            return false;
        }
        else{
            return true;
        }
    }

    private long addIdDb(String pid,String name,String email,String department,String designation){
        ContentValues cv=new ContentValues();
        cv.put(RegisterContract.RegisterEntry.COLUMN_PUI,pid);
        cv.put(RegisterContract.RegisterEntry.COLUMN_NAME,name);
        cv.put(RegisterContract.RegisterEntry.COLUMN_EMAIL,email);
        cv.put(RegisterContract.RegisterEntry.COLUMN_DEPARTMENT,department);
        cv.put(RegisterContract.RegisterEntry.COLUMN_DESIGNATION,designation);
        cv.put(RegisterContract.RegisterEntry.COLUMN_MEETING_COUNT,"0");
        return mDb.insert(RegisterContract.RegisterEntry.TABLE_NAME,null,cv);
    }


}
