package com.example.android.trackme;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.trackme.model.MeetingDetails;
import com.example.android.trackme.model.StatusReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UploadMeetingActivity extends AppCompatActivity {

    private EditText mClientName,mDate,mActivity,mTime;
    private DatabaseReference databaseReference,mMCRef,mStatus;
    private Long mMC;
    private DatePickerDialog.OnDateSetListener mDateSetListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_meeting);


        Intent intent=getIntent();
        String pui=intent.getStringExtra("pui");

        mClientName=(EditText) findViewById(R.id.name);
        mDate=(EditText) findViewById(R.id.date_new_meeting);
        mActivity=(EditText) findViewById(R.id.activity_new_meeting);
        mTime=(EditText) findViewById(R.id.time_new_meeting);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UploadMeetingActivity.this,
                        R.style.datepicker,
                        mDateSetListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.CYAN));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d("sd", String.valueOf(year + month + dayOfMonth));
                String date = String.valueOf(year + "/" + month + "/" + dayOfMonth);
                mDate.setText(String.valueOf(date));

            }
        };

        mClientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    mClientName.setError("Enter Client Name");
                } else {
                    mClientName.setError(null);
                }
            }
        });

        mDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mDate.setError("Enter Date");
                } else {
                    mDate.setError(null);
                }
            }
        });

        mActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mDate.setError("Enter Activity");
                } else {
                    mDate.setError(null);
                }
            }
        });

        mTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mDate.setError("Enter Time");
                } else {
                    mDate.setError(null);
                }
            }
        });



        databaseReference= FirebaseDatabase.getInstance().getReference();
        mMCRef=databaseReference.child("users").child(pui).child("userMC");
        mStatus=databaseReference.child("users").child(pui).child("userM");

        mMCRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMC=(Long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void send(View view) {
        String clientName = mClientName.getText().toString();
        String date = mDate.getText().toString();
        String clientActivity = mActivity.getText().toString();
        String clientTime=mTime.getText().toString();


        boolean cname = validateClientName(clientName);
        boolean cdate = validateDate(date);
        boolean cact = validateClientActivity(clientActivity);
        boolean ctime=validateClientTime(clientTime);
        if (!cname) {
            mClientName.setError("Enter Name");

        }

        if (!cdate) {
            mDate.setError("Enter Date");

        }

        if (!cact) {
            mActivity.setError("Enter Client Activity");

        }
        if (!ctime){
            mTime.setError("Enter Time");
        }



        if (!cdate || !cact || !cname ||!ctime) {
            return;
        }

        //Insert in firebase
     /*
        StatusReport statusReport=new StatusReport(clientName,date,clientLocation,spinnerAnswer);
        Map<String,Object> map=new HashMap<String, Object>();
        map.put(String.valueOf(mSR),statusReport);
        mStatus.updateChildren(map);
        mSR++;
        mSRRef.setValue(Integer.valueOf(String.valueOf(mSR)));
        */
        MeetingDetails meetingDetails=new MeetingDetails(date,clientActivity,clientName,clientTime,"0","0","0","0","false");
        Map<String,Object> map=new HashMap<String, Object>();
        map.put(String.valueOf(mMC),meetingDetails);
        mStatus.updateChildren(map);
        mMC++;
        mMCRef.setValue(Integer.valueOf(String.valueOf(mMC)));
        Toast.makeText(getApplicationContext(),"Meeting details successfully send to admin",Toast.LENGTH_SHORT).show();
        finish();



    }
    private boolean validateClientName(String name){
        if(name.length()==0)
            return false;
        else
            return true;
    }

    private boolean validateDate(String date){
        if(date.length()==0)
            return false;
        else
            return true;
    }

    private boolean validateClientActivity(String activity){

        if(activity.length()==0)
            return false;
        else
            return true;
    }
    private boolean validateClientTime(String time){
        if(time.length()==0)
            return false;
        else
            return true;
    }

}
