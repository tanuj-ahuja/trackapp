package com.example.android.trackme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;
import com.example.android.trackme.utils.BitmapUtils;
import com.example.android.trackme.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private ProgressBar mProgressBar,mUploadBar;
    LocationManager locationManager;
    Double lat,lng;
    private Toolbar mToolbar;
    private DatabaseReference databaseReference;
    private SQLiteDatabase mDb;
    private String mPUI;
    private Long mMeetingCount;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";

    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;
    private StorageReference mStorageReference;
    private Cursor mCursor;
    private Long mPC,mMeetNum;
    private DatabaseReference mPCRef,mMCRef;
    private Toast mToast;
    private int mFlag=0;
    DecimalFormat oneDigit = new DecimalFormat("#,##0");
    private TextView ui_hot = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mToolbar= (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        mProgressBar=(ProgressBar) findViewById(R.id.map_progress_bar);
        mUploadBar=(ProgressBar) findViewById(R.id.upload_progress_bar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(	36,	193	,238));
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







        RegisterDbHelper dbHelper=new RegisterDbHelper(this);
        mDb=dbHelper.getReadableDatabase();

        String[] projection = {
                RegisterContract.RegisterEntry._ID,
                RegisterContract.RegisterEntry.COLUMN_PUI,
                RegisterContract.RegisterEntry.COLUMN_MEETING_COUNT
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
        mPUI=mCursor.getString(1);
        Log.d("qqqqqqqqqq",mPUI);
        mCursor.close();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mPCRef=databaseReference.child("users").child(String.valueOf(mPUI)).child("countPic");
        mPCRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPC= (Long) dataSnapshot.getValue();

                Log.d("dvfdv",String.valueOf(mPC));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMCRef=databaseReference.child("users").child(String.valueOf(mPUI)).child("meetingcount");
        mMCRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMeetingCount= (Long) dataSnapshot.getValue();

                Log.d("dvfdv",String.valueOf(mPC));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        if(savedInstanceState!=null) {
            lat = savedInstanceState.getDouble("latitude");
            lng = savedInstanceState.getDouble("longitude");

        }





        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d("fds", "KKKK");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.d("Requeted","Permission");

                } else {


                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);


                }

                Log.d("dfg", "HETT");

                return;
            }

            else {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Double latitude = location.getLatitude();
                        Double longitute = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitute);
                //        Geocoder geocoder = new Geocoder(getApplicationContext());
                        lat = latitude;
                        lng = longitute;

                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        try {
                            DatabaseReference myref = databaseReference.child(Constants.FIREBASE_LOCATION).child(mPUI).child("latLng");
                            myref.setValue(latLng);
                        } catch (Exception e) {

                        }

/*
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitute, 1);
                            String str =addressList.get(0).getAddressLine(0)+","+addressList.get(0).getLocality() + ",";
                            str += addressList.get(0).getCountryName();
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            mProgressBar.setVisibility(View.INVISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    */
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
            }
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Double latitude = location.getLatitude();
                    Double longitute = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitute);
             //       Geocoder geocoder = new Geocoder(getApplicationContext());
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    try {
                        DatabaseReference myref = databaseReference.child(Constants.FIREBASE_LOCATION).child(mPUI).child("latLng");
                        myref.setValue(latLng);
                    }catch (Exception e){

                    }
                    /*
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitute, 1);
                        String str = addressList.get(0).getAddressLine(0)+","+addressList.get(0).getLocality() + ",";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    */
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    mProgressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstantState){
        savedInstantState.putString("uri",mTempPhotoPath);
        savedInstantState.putLong("picCount",mPC);
        mMeetNum=mMeetingCount;

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        mTempPhotoPath=savedInstanceState.getString("uri");
        mPC=savedInstanceState.getLong("picCount");
        if (String.valueOf(mMeetNum).equals("0")){
            ui_hot.setVisibility(View.INVISIBLE);
        }
        else  {
            ui_hot.setText(String.valueOf(mMeetNum));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*
        getMenuInflater().inflate(R.menu.options, menu);
        MenuItem item = menu.findItem(R.id.action_meetings_displays);
        MenuItemCompat.setActionView(item, R.layout.feed_update_count);

        notifCount = (Button) MenuItemCompat.getActionView(item);
        notifCount.setText(mMeetingCount);
        notifCount.setTextColor(Color.RED);


*/
        getMenuInflater().inflate(R.menu.options, menu);
        MenuItem item = menu.findItem(R.id.action_meetings_displays);
        MenuItemCompat.setActionView(item, R.layout.action_bar_notification_icon);

        final View menu_hotlist = menu.findItem(R.id.action_meetings_displays).getActionView();
        ui_hot = (TextView) menu_hotlist.findViewById(R.id.hotlist_hot);
        mMeetNum=mMeetingCount;
        if (String.valueOf(mMeetingCount).equals("0")){
            ui_hot.setVisibility(View.INVISIBLE);
        }
        else  {
            ui_hot.setText(String.valueOf(mMeetingCount));
        }
        new MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MapsActivity.this,MainActivity.class);
                intent.putExtra("pui",mPUI);
                startActivity(intent);
            }
        };

        return super.onCreateOptionsMenu(menu);




    }
    /*
    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    ui_hot.setVisibility(View.INVISIBLE);
                else {
                    ui_hot.setVisibility(View.VISIBLE);
                    ui_hot.setText(Integer.toString(new_hot_number));
                }
            }
        });
    }
    */


    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override abstract public void onClick(View v);

        @Override public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_details) {
            Intent intent=new Intent(this,DetailsActivity.class);
            intent.putExtra("pui",mPUI);
            startActivity(intent);
            return true;
        }

       else if(id==R.id.action_camera){
            // Check for the external storage permission
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // If you do not have permission, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            } else {
                // Launch the camera if the permission exists
                launchCamera();
            }

        }
       else if(id==R.id.action_status_report){
            Log.d("Click","Status Report");
            Intent intent=new Intent(this,StatusReportActivity.class);
            intent.putExtra("pui",mPUI);
            startActivity(intent);
            return true;
        }


       else if(id==R.id.action_meetings_displays){
            Intent intent =new Intent(this,MainActivity.class);
            intent.putExtra("pui",mPUI);
            startActivity(intent);
        }

       else if(id==R.id.action_upload_meeting){
            Intent intent=new Intent(this,UploadMeetingActivity.class);
            intent.putExtra("pui",mPUI);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }







    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 99) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                Log.d("yyy", "PERMISSION GRANTED");

                Location dc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Double lat = dc.getLatitude();
                Double lng = dc.getLongitude();
                LatLng latLng = new LatLng(lat, lng);
             //   Geocoder geocoder = new Geocoder(getApplicationContext());


                databaseReference = FirebaseDatabase.getInstance().getReference();
                try {
                    DatabaseReference myref = databaseReference.child(Constants.FIREBASE_LOCATION).child(mPUI).child("latLng");
                    myref.setValue(latLng);
                } catch (Exception e) {

                }

/*
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                    String str = addressList.get(0).getAddressLine(0) + "," + addressList.get(0).getLocality() + ",";
                    str += addressList.get(0).getCountryName();
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    mProgressBar.setVisibility(View.INVISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                mProgressBar.setVisibility(View.INVISIBLE);


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    return;
                }

            }
        } else {
            // Permission was denied or request was cancelled
            Log.d("yy", "PERMISSION DENIED");
        }
        if(requestCode==REQUEST_STORAGE_PERMISSION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If you get permission, launch the camera
                launchCamera();
            } else {
                // If you do not get permission, show a Toast
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void launchCamera() {

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        Log.d("dsfv",String.valueOf(data));

if(requestCode!=RESULT_CANCELED) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        //   Bitmap bitmap=(Bitmap) data.getExtras().get("data");
        // Process the image and set it to the TextView
        //     processAndSetImage(bitmap);
        processAndSetImage();
        Log.d("DONE", "DONE");
    } else {

        // Otherwise, delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath);
    }
}

    }
    private void processAndSetImage() {


        // Resample the saved image to fit the ImageView
      //  mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);
        Intent intent=new Intent(this,ImageCaptionActivity.class);
        intent.putExtra("bitmappath",mTempPhotoPath);
        intent.putExtra("mPC",mPC);
        startActivity(intent);
       // mResultsBitmap=bitmap;
        /*
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mCursor.moveToFirst();
        String email;
        final String puid;
        String[] projection2 = {
                RegisterContract.RegisterEntry._ID,
                RegisterContract.RegisterEntry.COLUMN_PUI,
                RegisterContract.RegisterEntry.COLUMN_NAME,
                RegisterContract.RegisterEntry.COLUMN_EMAIL,
                RegisterContract.RegisterEntry.COLUMN_DEPARTMENT,
                RegisterContract.RegisterEntry.COLUMN_DESIGNATION
        };

        Cursor cursor = mDb.query(
                RegisterContract.RegisterEntry.TABLE_NAME,                     // The table to query
                projection2,                             // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        cursor.moveToFirst();
        email=cursor.getString(3);
        puid=cursor.getString(1);
        Log.d("fv",puid);

        StorageReference mountainsRef = mStorageReference.child(email+"_"+mPC+".jpg");
        Log.d("StoraheRef",String.valueOf(mountainsRef));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mResultsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();



        UploadTask uploadTask = mountainsRef.putBytes(data);
        mUploadBar.setVisibility(View.VISIBLE);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference pic=databaseReference.child("users").child(puid).child("picsurl");
                DatabaseReference countPic=databaseReference.child("users").child(puid).child("countPic");
                DatabaseReference flagpic=databaseReference.child("users").child(puid).child("flagPic");
                Map<String,Object> dat=new HashMap<String, Object>();
                Log.d("c",String.valueOf(mPC));
                dat.put(String.valueOf(mPC),downloadUrl.toString());

                pic.updateChildren(dat);
                mPC++;
                countPic.setValue(mPC);
                flagpic.setValue("1");
                mUploadBar.setVisibility(View.INVISIBLE);
                Toast toast=Toast.makeText(getApplicationContext(),"Successfully send to admin",Toast.LENGTH_LONG);
                toast.show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                if(mFlag==1){
                    mToast.cancel();
                }
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                mToast=Toast.makeText(getApplicationContext(),"Upload is " + oneDigit.format(progress) + "% done",Toast.LENGTH_SHORT);
                mToast.show();
                 mFlag=1;
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        });

*/



    }









    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager;
        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED&& locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Location dc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Double lat = dc.getLatitude();
            Double lng = dc.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            /*
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                String str = addressList.get(0).getAddressLine(0) + "," + addressList.get(0).getLocality() + ",";
                str += addressList.get(0).getCountryName();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                mProgressBar.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            mProgressBar.setVisibility(View.INVISIBLE);

        }




    }
    public void openMainActivity(View view){
        Intent intent =new Intent(this,MainActivity.class);
        intent.putExtra("pui",mPUI);
        startActivity(intent);
    }
}
