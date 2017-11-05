package com.example.android.trackme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.trackme.ActiveMeetingDetails.MeetingInfoActivity;
import com.example.android.trackme.Adapters.MeetingsListAdapter;
import com.example.android.trackme.model.MeetingDetails;
import com.example.android.trackme.sync.MyAlarm;
import com.example.android.trackme.sync.ReminderUtilities;
import com.example.android.trackme.utils.NotificationUtils;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListViewMeetings;
    private MeetingsListAdapter mActiveListAdapter;
    private String mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        mToolbar= (Toolbar) findViewById(R.id.my_toolbar);
        mListViewMeetings=(ListView) findViewById(R.id.list_view_meetings);

        final Intent intent=getIntent();
        final String pid=intent.getStringExtra("pui");
        if(pid!=null) {
            Log.d("push id", pid);
        }

        setAlarm(System.currentTimeMillis());


        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        DatabaseReference countMeeting=databaseReference.child("users").child(pid).child("meetingcount");

        countMeeting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long mC=(Long) dataSnapshot.getValue();
                TextView textViewNoMeetings=(TextView) findViewById(R.id.no_meetings);

                if(mC==0){
                    textViewNoMeetings.setVisibility(View.VISIBLE);

                }
                else {
                    textViewNoMeetings.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


      /*  DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        DatabaseReference meetingsUserRef=databaseReference.child("users").child(id).child("meetings");*/

        Firebase meetingsUserRef = new Firebase("https://trackme-22c21.firebaseio.com/users/"+pid+"/meetings");


        mActiveListAdapter = new MeetingsListAdapter(this, MeetingDetails.class,
                R.layout.list_item_meeting, meetingsUserRef);
        mListViewMeetings.setAdapter(mActiveListAdapter);

       mListViewMeetings.setOnItemClickListener(new AdapterView.OnItemClickListener(){

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               MeetingDetails selectedItem=mActiveListAdapter.getItem(position);
               if(selectedItem!=null){
                   String meetingId=mActiveListAdapter.getRef(position).getKey();
                   Intent intent=new Intent(MainActivity.this,MeetingInfoActivity.class);
                   intent.putExtra("meetingId",meetingId);
                   intent.putExtra("pui",pid);
                   intent.putExtra("clientn",selectedItem.getClientn());
                   intent.putExtra("date",selectedItem.getDate());
                   intent.putExtra("time",selectedItem.getTime());
                   intent.putExtra("activity",selectedItem.getActivity());
                   startActivity(intent);
               }
           }
       });


        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(	36,	193	,238));
        }

//        ReminderUtilities.scheduleFirebaseJobDispatcherSync(this);

    }

    private void setAlarm(long l) {
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(this, MyAlarm.class);
        PendingIntent p=PendingIntent.getBroadcast(this,0,i,0);
        alarmManager.setRepeating(AlarmManager.RTC,l,1000*60*1,p);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActiveListAdapter.cleanup();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_map) {
            Intent intent=new Intent(this,MapsActivity.class);

            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
