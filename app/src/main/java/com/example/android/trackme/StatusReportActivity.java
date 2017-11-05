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
import android.widget.Spinner;

import com.example.android.trackme.model.StatusReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusReportActivity extends AppCompatActivity {

    private DatabaseReference databaseReference,mSRRef,mStatus;
    private Long mSR;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText mClientName,mClientLocation,mDate;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_report);

        mClientName = (EditText) findViewById(R.id.client_name);
        mClientLocation = (EditText) findViewById(R.id.client_location);
        mDate = (EditText) findViewById(R.id.date_status_report);
        mSpinner=(Spinner) findViewById(R.id.status_report_spinner);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        StatusReportActivity.this,
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

        mClientLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    mClientLocation.setError("Enter Pan Number");
                } else {
                    mClientLocation.setError(null);
                }
            }
        });





        Intent intent=getIntent();
        String pui=intent.getStringExtra("pui");


        databaseReference=FirebaseDatabase.getInstance().getReference();
         mStatus=databaseReference.child("users").child(pui).child("statusReport");
        mSRRef=databaseReference.child("users").child(pui).child("countStatusReport");
        Log.d("f",String.valueOf(mSRRef));

        mSRRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSR=(Long) dataSnapshot.getValue();
                Log.d("fgfv",String.valueOf(mSR));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


/*
        Map<String,Object> map=new HashMap<String, Object>();
        map.put(String.valueOf(mSR),statusReport);

        mSR++;
        mSRRef.setValue(Integer.valueOf(String.valueOf(mSR)));
        */


    }
    public void submit(View view) {
        String clientName = mClientName.getText().toString();
        String date = mDate.getText().toString();
        String clientLocation = mClientLocation.getText().toString();
        String spinnerAnswer=mSpinner.getSelectedItem().toString();


        boolean cname = validateClientName(clientName);
        boolean cdate = validateDate(date);
        boolean cpan = validateClientLocation(clientLocation);
        if (!cname) {
            mClientName.setError("Enter Name");

        }

        if (!cdate) {
            mDate.setError("Enter Date");

        }

        if (!cpan) {
            mClientLocation.setError("Enter Client Location");

        }



        if (!cdate || !cpan || !cname ) {
            return;
        }

        //Insert in firebase
        StatusReport statusReport=new StatusReport(clientName,date,clientLocation,spinnerAnswer);
        Map<String,Object> map=new HashMap<String, Object>();
        map.put(String.valueOf(mSR),statusReport);
        mStatus.updateChildren(map);
        mSR++;
        mSRRef.setValue(Integer.valueOf(String.valueOf(mSR)));
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

    private boolean validateClientLocation(String panNumber){

        if(panNumber.length()==0)
            return false;
        else
            return true;
    }
}
