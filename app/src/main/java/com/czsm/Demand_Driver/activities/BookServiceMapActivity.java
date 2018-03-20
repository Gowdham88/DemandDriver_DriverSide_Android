package com.czsm.Demand_Driver.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.Demand_Driver.Firebasemodel.AppointmentList;
import com.czsm.Demand_Driver.Firebasemodel.ServiceList;
import com.czsm.Demand_Driver.Firebasemodel.ServiceproviderList;
import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.Retrofit.DistanceApi;
import com.czsm.Demand_Driver.controller.AllinAllController;
import com.czsm.Demand_Driver.helper.RESTClient;
import com.czsm.Demand_Driver.helper.Util;
import com.czsm.Demand_Driver.model.ServiceProvider;
import com.czsm.Demand_Driver.model.TimeModelClass;
import com.czsm.Demand_Driver.view.CustomDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookServiceMapActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,


        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {


    Toolbar toolbar;
    TextView serviceTextView;
    ImageView serviceImageView;
    LinearLayout driverLayout;
    Spinner driverTypeSpinner;
    AutoCompleteTextView carTypeSpinner;
    LinearLayout tariffLayout;
    TextView bookNowTextview;
    TextView bookLaterTextview;
    Button bookButton;

    private AllinAllController allinAllController;

    CustomDateTimePicker bookLaterDateTimePicker;
    String serviceId = "1";
    String serviceName = "Driver";
    String bookDate, bookTime, bookNow;
    public static String BaseUrl;


    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    ArrayList<ServiceProvider> providers = new ArrayList<ServiceProvider>();
    ArrayList<Float> distancelist = new ArrayList<Float>();

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int SURROUNDING_DISTANCE = 30000;
    protected static final String TAG = "GettingUserLocation";
    private Location userLocation;
    //    HashMap<ServiceProvider, Float> sortedDistances;
//    ArrayList<ServiceProvider> sortedProviders;
    SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    int markerIcon;
    List<Integer> minIndex = new ArrayList<Integer>();
    String distance, time, userid, username, usermobileno, useraddress, userimage = "";
    long timems;

    /**********Firebase**************/

    DatabaseReference db;
    ArrayList<ServiceList> filterserviceproviders = new ArrayList<ServiceList>();
    ArrayList<ServiceList> serviceproviders = new ArrayList<ServiceList>();
    ArrayList<AppointmentList> appointmentlist = new ArrayList<AppointmentList>();
    SharedPreferences sharedPreferences;
    int i = 1, maxproviders = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service_map);


        db = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("userId", "");
        username = sharedPreferences.getString("username", "");
        usermobileno = sharedPreferences.getString("usermobile", "");
        useraddress = sharedPreferences.getString("useraddress", "");
        userimage = sharedPreferences.getString("userimage", "");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        Toolbar toolbar = (Toolbar) findViewById(R.id.user_service_details_toolbar);
        toolbar.setTitle("Book Drivers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        serviceTextView = (TextView) findViewById(R.id.service_details_service_textview);
        serviceImageView = (ImageView) findViewById(R.id.service_details_service_imageview);
        driverLayout = (LinearLayout) findViewById(R.id.service_details_call_driver_linearlayout);
        driverTypeSpinner = (Spinner) findViewById(R.id.service_details_driver_type_spinner);
        carTypeSpinner = (AutoCompleteTextView) findViewById(R.id.service_details_car_type_spinner);
        tariffLayout = (LinearLayout) findViewById(R.id.service_details_tariff_linearlayout);
        bookNowTextview = (TextView) findViewById(R.id.service_details_book_now_textview);
        bookLaterTextview = (TextView) findViewById(R.id.service_details_book_later_textview);
        bookButton = (Button) findViewById(R.id.service_details_book_button);


        setMarkerIcon(serviceId);
        if (!serviceId.equalsIgnoreCase("1")) {
            driverLayout.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            driverLayout.setVisibility(View.INVISIBLE);
            carTypeSpinner.setVisibility(View.INVISIBLE);
            driverTypeSpinner.setVisibility(View.INVISIBLE);
        }

        serviceImageView.setImageResource(R.drawable.services_call_driver);
        serviceTextView.setText(serviceName);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.car_type_array));
        carTypeSpinner.setThreshold(1);
        carTypeSpinner.setAdapter(adapter);

        carTypeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                carTypeSpinner.showDropDown();
            }
        });

        tariffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent tariffIntent = new Intent(getApplicationContext(), TariffPlanActivity.class);
                tariffIntent.putExtra("serviceId", serviceId);
                startActivity(tariffIntent);

            }
        });


        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
        bookNowTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterserviceproviders.size() > 0) {
                    if (!serviceId.equalsIgnoreCase("1") || !carTypeSpinner.getText().toString().equals("Select car type")) {
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");

                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                        stf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

                        bookDate = sdf.format(now);
                        bookTime = stf.format(now);
                        bookNow = "1";
                        Distance();

                    } else
                        Toast.makeText(getApplicationContext(), "Must select car type and options", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "We are sorry, there is no service providers for this service at your region.", Toast.LENGTH_LONG).show();
            }
        });

        bookLaterDateTimePicker = getCustomDateTimePicker(bookLaterTextview);
        bookLaterDateTimePicker.set24HourFormat(true);
        bookLaterDateTimePicker.setDate(Calendar.getInstance());
        bookLaterTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (providers.size() > 0)

                    bookLaterDateTimePicker.showDialog();
                else
                    Toast.makeText(getApplicationContext(), "We are sorry, there is no service providers for this service at your region.", Toast.LENGTH_LONG).show();

            }
        });


    }


    private void setMarkerIcon(String serviceId) {

        markerIcon = R.drawable.lmdriver;

    }

    private CustomDateTimePicker getCustomDateTimePicker(final TextView textView) {
        CustomDateTimePicker custom = new CustomDateTimePicker(BookServiceMapActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {

            @Override
            public void onSet(Dialog dialog, final Calendar calendarSelected,
                              Date dateSelected, int year, String monthFullName,
                              String monthShortName, int monthNumber, int date,
                              String weekDayFullName, String weekDayShortName,
                              int hour24, int hour12, int min, int sec,
                              String AM_PM) {
                textView.setText(calendarSelected
                        .get(Calendar.DAY_OF_MONTH)
                        + "/" + (monthNumber + 1) + "/" + year
                        + ", " + hour12 + ":" + min
                        + " " + AM_PM);
                bookButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!serviceId.equalsIgnoreCase("1") || !carTypeSpinner.getText().toString().equals("Select car type")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
                            bookDate = sdf.format(calendarSelected.getTime());
                            bookTime = stf.format(calendarSelected.getTime());
                            bookNow = "0";
                            Distance();
                        } else
                            Toast.makeText(BookServiceMapActivity.this, "Must select car type and options", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancel() {
            }
        });
        return custom;
    }

    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(BookServiceMapActivity.this)
                //set message, title, and icon
                .setTitle("Booking")
                .setMessage("Do you want to book service at " + Util.getDateTime(bookDate, bookTime) + " ?" + "\n" + distance + "\n" + time)
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /******************Adding appointments**********************/

                        addappointment(0);

                        Query myTopPostsQuery = db.child("AppointmentList").orderByChild("timems").equalTo(timems);
                        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                                    final AppointmentList appointmentList = child.getValue(AppointmentList.class);

                                    appointmentlist.clear();

                                    if (appointmentList.getUserid().equals(userid)) {

                                        appointmentlist.add(appointmentList);

                                        if (appointmentlist.get(0).getStatus().equals("pending") && i == 1) {

                                            showsuccessDialog();

                                        }

                                        if (i < maxproviders) {

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if (appointmentlist.get(0).getStatus().equals("pending")) {

                                                        Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();

                                                        if (i == 1) {

                                                            Map appointmentData = new HashMap();
                                                            appointmentData.put("cancelid", filterserviceproviders.get(minIndex.get(0)).getProviderid());
                                                            appointmentData.put("providerid", filterserviceproviders.get(minIndex.get(i)).getProviderid());
                                                            appointmentData.put("drivername", filterserviceproviders.get(minIndex.get(i)).getName());
                                                            appointmentData.put("driveraddress", filterserviceproviders.get(minIndex.get(i)).getAddress());
                                                            child.getRef().updateChildren(appointmentData);

                                                        } else {

                                                            Map appointmentData = new HashMap();
                                                            appointmentData.put("cancelid", appointmentlist.get(0).getCancelid() + "," + filterserviceproviders.get(minIndex.get(i)).getProviderid());
                                                            appointmentData.put("providerid", filterserviceproviders.get(minIndex.get(i)).getProviderid());
                                                            appointmentData.put("drivername", filterserviceproviders.get(minIndex.get(i)).getName());
                                                            appointmentData.put("driveraddress", filterserviceproviders.get(minIndex.get(i)).getAddress());
                                                            child.getRef().updateChildren(appointmentData);

                                                        }

                                                        i++;

                                                    }

                                                }
                                            }, 10000);

                                        } else {

                                            if (appointmentlist.get(0).getStatus().equals("pending") && i == maxproviders) {

                                                child.getRef().child("status").setValue("Cancelled");
                                            }
                                        }

                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.e("Databaseerror", "" + databaseError.toException().toString());

                            }
                        });

                    }
                })

                /***************************************************************/

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).show();
    }

    protected void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.setAlwaysShow(true);
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    protected void startLocationUpdates() {

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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );

    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        );
    }


    @Override
    public void onMapReady(GoogleMap Map) {
        Log.d("Locationnnnnnn ", "Latit " + userLocation.getLatitude() + " long: " + userLocation.getLongitude());

        googleMap = Map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (serviceproviders.size() > 0) {
            getSurroundingProviders();
            Log.e("Size", "" + filterserviceproviders.size());
            int numOfProviders = filterserviceproviders.size();
            Double[] latitude = new Double[numOfProviders];
            Double[] longitude = new Double[numOfProviders];
            Marker[] markers = new Marker[numOfProviders];

            try {
                for (int i = 0; i < numOfProviders; i++) {

                    Log.e("lat", "" + filterserviceproviders.get(i).getLatitude());
                    latitude[i] = Double.valueOf(filterserviceproviders.get(i).getLatitude());
                    longitude[i] = Double.valueOf(filterserviceproviders.get(i).getLongitude());
                    markers[i] = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude[i], longitude[i]))
                            .title(filterserviceproviders.get(i).getName())
                            .icon(BitmapDescriptorFactory.fromResource(markerIcon))
                    );

                }
                if (markers.length > 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    int width = displayMetrics.widthPixels;
                    int height = displayMetrics.heightPixels;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width - 200, height - 300, 50);
                    googleMap.animateCamera(cu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private ArrayList<ServiceList> getSurroundingProviders() {
        HashMap<ServiceList, Float> distances = new HashMap<ServiceList, Float>();

        for (ServiceList prov : serviceproviders) {
            Location providerLocation = new Location(prov.getName());
            providerLocation.setLatitude(Double.parseDouble(prov.getLatitude()));
            providerLocation.setLongitude(Double.parseDouble(prov.getLongitude()));
            float distance = userLocation.distanceTo(providerLocation);
            Log.e(getString(R.string.tag), "distance: " + distance);
            distances.put(prov, distance);
            if (distance <= SURROUNDING_DISTANCE) {
                filterserviceproviders.add(prov);
                distancelist.add(distance);
//
            }
        }

        try {

            ArrayList<Float> nstore = new ArrayList<Float>(distancelist);
            Collections.sort(distancelist);

            for (int i = 0; i < distancelist.size(); i++) {

                minIndex.add(nstore.indexOf(distancelist.get(i)));

                Log.e("ssssssss", "" + minIndex.get(i));
                Log.e("ssssssss", "" + filterserviceproviders.get(i).getProviderid());

            }

            maxproviders = filterserviceproviders.size();

        } catch (NullPointerException e) {

            e.printStackTrace();
        }


        if (filterserviceproviders.size() <= 0)
            Toast.makeText(getApplicationContext(), "We are sorry, there is no service providers for this service at your region", Toast.LENGTH_LONG).show();
//        sortDistances(distances);


        return filterserviceproviders;
    }


    @Override
    public void sendServiceResult(String serviceResult) {
        Toast.makeText(getApplicationContext(), serviceResult, Toast.LENGTH_LONG).show();
        if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_book_success))) {
//            finish();
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(getApplicationContext());
    }

    private static final class ValueComparator<V extends Comparable<? super V>>
            implements Comparator<Map.Entry<?, V>> {
        public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (userLocation == null) {
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
            userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (userLocation != null) {
                mapFragment.getMapAsync(this);
                ValueEventListener maplistner = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            ServiceproviderList user = child.getValue(ServiceproviderList.class);

                            if(user.getStatus().equals("free")) {

                                ServiceList serviceitem = new ServiceList();
                                serviceitem.setLatitude(user.getLatitude());
                                serviceitem.setLongitude(user.getLongitude());
                                serviceitem.setAddress(user.getAddress());
                                serviceitem.setName(user.getName());
                                serviceitem.setProviderid(child.getKey());
                                serviceitem.setDriverimage(user.getProfilepic());
                                serviceproviders.add(serviceitem);

                            }

                        }

                        onMapReady(googleMap);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e("loadPost:onCancelled", databaseError.toException().toString());
                    }
                };

                db.child("ServiceproviderList").orderByChild("application_usage_count").addListenerForSingleValueEvent(maplistner);
                stopLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (userLocation == null) {
            userLocation = location;
            mapFragment.getMapAsync(this);
            stopLocationUpdates();
            stopLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings.");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(BookServiceMapActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
//        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected())
//            mGoogleApiClient.disconnect();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            // Check for the integer request code originally supplied to startResolutionForResult().
//            case REQUEST_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Log.i(TAG, "User agreed to make required location settings changes.");
//                        startLocationUpdates();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Log.i(TAG, "User choose not to make required location settings changes.");
//                        showCanNotCompleteDialog();
//                        break;
//                }
//                break;
//        }
//    }

    private void showsuccessDialog() {
        new android.support.v7.app.AlertDialog.Builder(BookServiceMapActivity.this)
                //set message, title, and icon
                .setTitle("Appointment")
                .setMessage("Appointment booked successfully.")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).show();
    }



    public void Distance() {

        final ProgressDialog dialog = ProgressDialog.show(this,"Fetching data","Please wait...",false,false);

        StringBuilder googlePlacesUrl = new StringBuilder("api/distancematrix/json?");
        googlePlacesUrl.append("origins=" + userLocation.getLatitude() + "," + userLocation.getLongitude());
        googlePlacesUrl.append("&destinations=" + filterserviceproviders.get(minIndex.get(0)).getLatitude() + "," + filterserviceproviders.get(minIndex.get(0)).getLongitude());
        googlePlacesUrl.append("&key=" + "AIzaSyDv2rBW15Rnox8k13gIrgr5ksSerLqf2T0");

        Log.e("dsdjsh",filterserviceproviders.get(minIndex.get(0)).getLatitude()+filterserviceproviders.get(minIndex.get(0)).getLongitude());

        BaseUrl = googlePlacesUrl.toString();

//        RestAdapter adapter = new RestAdapter.Builder()
//                .setEndpoint("https://maps.googleapis.com/maps")
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .build();


       Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        DistanceApi distanceApi =  retrofit.create(DistanceApi.class);

        Call<TimeModelClass> call = distanceApi.getUsers(BaseUrl);
        call.enqueue(new Callback<TimeModelClass>() {
            @Override
            public void onResponse(Call<TimeModelClass> call, Response<TimeModelClass> response) {

                if(response.body().getStatus().equals("OK")){

                    try {

                        Log.e("Dsdsds", response.body().getRows().get(0).getElements().get(0).getDistance().getText());

                        distance = "Distance :" + response.body().getRows().get(0).getElements().get(0).getDistance().getText();
                        time = "Expected time :" + response.body().getRows().get(0).getElements().get(0).getDuration().getText();
                        dialog.dismiss();
                        showConfirmDialog();

                    } catch (NullPointerException e){

                        e.printStackTrace();
                    }


                } else {

                    dialog.dismiss();
                    showConfirmDialog();
                }

            }

            @Override
            public void onFailure(Call<TimeModelClass> call, Throwable t) {

                dialog.dismiss();
                showConfirmDialog();

            }
        });

    }

    public void addappointment(int position){

        Calendar currentCal = Calendar.getInstance();
        timems = currentCal.getTimeInMillis();

        AppointmentList apointmentlist = new AppointmentList();
        apointmentlist.setUserid(userid);
        apointmentlist.setUsername(username);
        apointmentlist.setUsermobilenumber(usermobileno);
        apointmentlist.setUseraddress(useraddress);
        apointmentlist.setUserimage(userimage);
        apointmentlist.setUserreview("0");
        apointmentlist.setProviderid(filterserviceproviders.get(minIndex.get(position)).getProviderid());
        apointmentlist.setDrivername(filterserviceproviders.get(minIndex.get(position)).getName());
        apointmentlist.setDriveraddress(filterserviceproviders.get(minIndex.get(position)).getAddress());
        apointmentlist.setDriverimage(filterserviceproviders.get(minIndex.get(position)).getDriverimage());
        apointmentlist.setCartype(carTypeSpinner.getText().toString());
        apointmentlist.setHourly_or_workstation(driverTypeSpinner.getSelectedItem().toString());
        apointmentlist.setDate(bookDate);
        apointmentlist.setTime(bookTime);
        apointmentlist.setStatus("pending");
        apointmentlist.setUser_latitude(String.valueOf(userLocation.getLatitude()));
        apointmentlist.setUser_longitude(String.valueOf(userLocation.getLongitude()));
        apointmentlist.setBook_now(bookNow);
        apointmentlist.setCancelid("");
        apointmentlist.setTimems(timems);


        db.child("AppointmentList").push().setValue(apointmentlist);

    }





}

