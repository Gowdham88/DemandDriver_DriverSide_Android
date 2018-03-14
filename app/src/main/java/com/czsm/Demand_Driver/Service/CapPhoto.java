package com.czsm.Demand_Driver.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.czsm.Demand_Driver.Firebasemodel.LocationList;
import com.czsm.Demand_Driver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CapPhoto extends Service
{
    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Parameters parameters;
    private boolean safeToTakePicture = false;
    StorageReference mountainsRef;
    StorageReference mountainImagesRef;
    FirebaseStorage storage;

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 50000;
    private static final float LOCATION_DISTANCE = 0;

    DatabaseReference db;
    SharedPreferences preferences;
    String name,id,userid,userimage = "";
    LocationList updatelocation;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("CAM", "start");
        initializeLocationManager();
        db          = FirebaseDatabase.getInstance().getReference();
        preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);


        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        storage = FirebaseStorage.getInstance();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);};
        Thread myThread = null;


    }
    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

        if (Camera.getNumberOfCameras() >= 2) {

            mCamera = Camera.open();
        }

        if (Camera.getNumberOfCameras() < 2) {

            mCamera = Camera.open();
        }
        SurfaceView sv    = new SurfaceView(getApplicationContext());
        SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);

        try {

            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();
            mCamera.setParameters(parameters);
            mCamera.setPreviewTexture(st);
            mCamera.startPreview();

//            mCamera.takePicture(null, null, mCall);
        } catch (IOException e) {

            e.printStackTrace();

        }

        sHolder = sv.getHolder();
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);


        if(intent != null) {

            try {

                id     = intent.getStringExtra("id");
                name   = intent.getStringExtra("name");
                userid = intent.getStringExtra("userid");

            } catch (NullPointerException e) {

                e.printStackTrace();
            }

        }

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
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
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            updatelocation = new LocationList();

            try {

                mCamera.takePicture(null, null, mCall);

                if(!userimage.equals("")) {

                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    updatelocation.setName(name);
                    updatelocation.setUserid(userid);
                    updatelocation.setId(id);
                    updatelocation.setTime(currentDateTimeString);
                    updatelocation.setLatitude(location.getLatitude());
                    updatelocation.setLongitude(location.getLongitude());
                    updatelocation.setImage(userimage);
                    db.child("LocationList").push().setValue(updatelocation);

                }

            } catch (Exception e) {

                e.printStackTrace();
            }





        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.e(TAG, "onProviderDisabled: " + provider);

        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.e(TAG, "onProviderEnabled: " + provider);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            Log.e(TAG, "onStatusChanged: " + provider);

        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {

        public void onPictureTaken(final byte[] data, Camera camera) {

            try {

                Calendar cal         = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String tar           = (sdf.format(cal.getTime()));

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-demanddriver.appspot.com");
                mountainsRef = storageRef.child("LiveImages");

                // Create a reference to 'images/mountains.jpg'
                mountainImagesRef = storageRef.child("images.jpg");


                UploadTask uploadTask = mountainImagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        userimage = taskSnapshot.getDownloadUrl().toString();
                        Log.e("sasasasa","sasa"+userimage);


                    }
                });


                camkapa(sHolder);


            } catch (NullPointerException e) {

                Log.d("CAM", e.getMessage());

            }

        }
    };


}


