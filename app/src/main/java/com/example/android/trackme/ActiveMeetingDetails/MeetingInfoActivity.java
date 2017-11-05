package com.example.android.trackme.ActiveMeetingDetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.trackme.R;
import com.example.android.trackme.model.MeetingDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MeetingInfoActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference, mFlag,mCheckInT,mCkeckOutT;
    private TextView mClientN, mDate, mTime, mActivity,mStatus,mStartTime,mEndTime;
    private Button mCheckIn;
    private String flag;
    private Toolbar mToolbar;
    private int check = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);

        mClientN = (TextView) findViewById(R.id.text_view_client_name_value);
        mDate = (TextView) findViewById(R.id.date_value);
        mTime = (TextView) findViewById(R.id.time_value);
        mActivity = (TextView) findViewById(R.id.activity_value);
        mCheckIn = (Button) findViewById(R.id.button_checkin);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_meeting_details);
        mStartTime=(TextView) findViewById(R.id.chech_in_value);
        mEndTime=(TextView) findViewById(R.id.check_out_value);
        mStatus=(TextView) findViewById(R.id.value_flag);
        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(36, 193, 238));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked

                finish();
            }
        });

        Intent intent = getIntent();
        String pui = intent.getStringExtra("pui");
        String meetingId = intent.getStringExtra("meetingId");
        String clientn = intent.getStringExtra("clientn");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String activity = intent.getStringExtra("activity");


        mClientN.setText(clientn);
        mDate.setText(date);
        mTime.setText(time);
        mActivity.setText(activity);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(pui).child("meetings").child(meetingId);


        mCheckInT=mDatabaseReference.child("start");
        mCheckInT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String startTime=dataSnapshot.getValue().toString();
                if(!startTime.equals("0")){
                    mStartTime.setText(startTime);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCkeckOutT=mDatabaseReference.child("end");
        mCkeckOutT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String endTime=dataSnapshot.getValue().toString();
                if(!endTime.equals("0")){
                    mEndTime.setText(endTime);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        mFlag = mDatabaseReference.child("flag");
        mFlag.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = dataSnapshot.getValue().toString();
                if (flag.equals("0")) {
                    check = 0;
                    mStatus.setText("Open");
                }
                else if(flag.equals("Punched In")){
                    check=2;
                    mStatus.setText("Punched In");
                }
                Log.d("flag", flag);
                if (check == 0) {
                    mCheckIn.setVisibility(View.VISIBLE);
                }
                else if(check==2){
                    mCheckIn.setVisibility(View.VISIBLE);
                    mCheckIn.setText("Punch Out");
                    check=1;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void checkin(View view) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d("flag", flag);
        if (flag.equals("0")) {
            DatabaseReference checkInRef = mDatabaseReference.child("start");
            DatabaseReference locationRef= mDatabaseReference.child("location");
            checkInRef.setValue(currentDateTimeString);
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location dc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Double lat = dc.getLatitude();
            Double lng = dc.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                String str = addressList.get(0).getAddressLine(0) + "," + addressList.get(0).getLocality() + ",";
                str += addressList.get(0).getCountryName();
                Log.d("Location",str);
                locationRef.setValue(str);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            mFlag.setValue("Punched In");
            mCheckIn.setText("Punch Out");
            mStatus.setText("Punched In");
            Toast.makeText(getApplicationContext(),"Successfully Punched In",Toast.LENGTH_SHORT).show();
        }
        else if(flag.equals("Punched In")){
            DatabaseReference checkOutRef=mDatabaseReference.child("end");
            checkOutRef.setValue(currentDateTimeString);
            mFlag.setValue("Punched Out");
            mStatus.setText("Punched Out");
            Toast.makeText(getApplicationContext(),"Successfully Punched Out",Toast.LENGTH_SHORT).show();
            mCheckIn.setVisibility(View.GONE);
        }
    }
}
