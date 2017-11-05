package com.example.android.trackme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.trackme.data.RegisterContract;
import com.example.android.trackme.data.RegisterDbHelper;
import com.example.android.trackme.model.Picture;
import com.example.android.trackme.utils.BitmapUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ImageCaptionActivity extends AppCompatActivity {

    private Bitmap mBitmap;
    private String mPhotoPath;
    private ImageView mImageView;
    private EditText mCaption,mDesc;
    private Cursor mCursor;
    private DatabaseReference databaseReference;
    private SQLiteDatabase mDb;
    private Toast mToast;
    private int mFlag=0;
    private ProgressBar mUploadBar;
    private Long mPC;
    private StorageReference mStorageReference;
    DecimalFormat oneDigit = new DecimalFormat("#,##0");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_caption);

        mImageView=(ImageView) findViewById(R.id.bitmap);
        mCaption=(EditText) findViewById(R.id.caption);
        mDesc=(EditText) findViewById(R.id.desc);
        mUploadBar=(ProgressBar) findViewById(R.id.upload_progress_bar);


        databaseReference = FirebaseDatabase.getInstance().getReference();


        Intent intent=getIntent();
        mPhotoPath=intent.getStringExtra("bitmappath");
        mPC=intent.getLongExtra("mPC",0);
        mBitmap= BitmapUtils.resamplePic(ImageCaptionActivity.this,mPhotoPath);
        mImageView.setImageBitmap(mBitmap);





    }

    public void upload(View view) {

        final String caption=mCaption.getText().toString();
        final String desc=mDesc.getText().toString();

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
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                Picture picture=new Picture(downloadUrl.toString(),caption,desc);
                dat.put(String.valueOf(mPC),picture);



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
                mToast=Toast.makeText(getApplicationContext(),"Upload is " + oneDigit.format(progress) + "% done", Toast.LENGTH_SHORT);
                mToast.show();
                 mFlag=1;
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        });

        finish();



    }
}