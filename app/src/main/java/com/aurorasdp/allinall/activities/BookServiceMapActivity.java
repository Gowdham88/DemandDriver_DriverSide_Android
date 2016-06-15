package com.aurorasdp.allinall.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.ServiceProvider;
import com.aurorasdp.allinall.view.CustomDateTimePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookServiceMapActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {

    @InjectView(R.id.user_service_details_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.service_details_service_textview)
    TextView serviceTextView;
    @InjectView(R.id.service_details_service_imageview)
    ImageView serviceImageView;

    @InjectView(R.id.service_details_call_driver_linearlayout)
    LinearLayout driverLayout;

    @InjectView(R.id.service_details_driver_type_spinner)
    Spinner driverTypeSpinner;
    @InjectView(R.id.service_details_car_type_spinner)
    Spinner carTypeSpinner;

    @InjectView(R.id.service_details_tariff_linearlayout)
    LinearLayout tariffLayout;

    @InjectView(R.id.service_details_book_now_textview)
    TextView bookNowTextview;
    @InjectView(R.id.service_details_book_later_textview)
    TextView bookLaterTextview;
    @InjectView(R.id.service_details_book_button)
    Button bookButton;

    private AllinAllController allinAllController;

    CustomDateTimePicker bookLaterDateTimePicker;
    String serviceId, serviceName, bookDate, bookTime, bookNow;
    int serviceImageId;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int SURROUNDING_DISTANCE = 30000;
    protected static final String TAG = "GettingUserLocation";
    private Location userLocation;
    //    HashMap<ServiceProvider, Float> sortedDistances;
//    ArrayList<ServiceProvider> sortedProviders;
    SupportMapFragment mapFragment;
    int markerIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service_map);
        ButterKnife.inject(this);
        allinAllController = new AllinAllController(this, this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.book_appointment);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            serviceId = extras.getString("serviceId");
            serviceName = extras.getString("serviceName");
            serviceImageId = extras.getInt("serviceImageId");
            setMarkerIcon(serviceId);
            if (!serviceId.equalsIgnoreCase("1")) {
                driverLayout.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                driverLayout.setVisibility(View.INVISIBLE);
                carTypeSpinner.setVisibility(View.INVISIBLE);
                driverTypeSpinner.setVisibility(View.INVISIBLE);
            }
            serviceImageView.setImageResource(serviceImageId);
            serviceTextView.setText(serviceName);
        }
        tariffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tariffIntent = new Intent(getApplicationContext(), TariffPlanActivity.class);
                tariffIntent.putExtra("serviceId", serviceId);
                startActivity(tariffIntent);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
        bookNowTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!serviceId.equalsIgnoreCase("1") || carTypeSpinner.getSelectedItemPosition() > 0) {

                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");

                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                    stf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

                    bookDate = sdf.format(now);
                    bookTime = stf.format(now);
                    bookNow = "1";
                    showConfirmDialog();
                } else
                    Toast.makeText(getApplicationContext(), "Must select car type and options", Toast.LENGTH_LONG).show();
            }
        });

        bookLaterDateTimePicker = getCustomDateTimePicker(bookLaterTextview);
        bookLaterDateTimePicker.set24HourFormat(true);
        bookLaterDateTimePicker.setDate(Calendar.getInstance());
        bookLaterTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookLaterDateTimePicker.showDialog();
            }
        });
    }

    private void setMarkerIcon(String serviceId) {
        if (serviceId.equalsIgnoreCase("1"))
            markerIcon = R.drawable.lmdriver;
        else if (serviceId.equalsIgnoreCase("2"))
            markerIcon = R.drawable.lmcarbike;
        else if (serviceId.equalsIgnoreCase("3"))
            markerIcon = R.drawable.lmplumber;
        else if (serviceId.equalsIgnoreCase("4"))
            markerIcon = R.drawable.lmelectrician;
        else if (serviceId.equalsIgnoreCase("5"))
            markerIcon = R.drawable.lmcarpenter;
    }

    private CustomDateTimePicker getCustomDateTimePicker(final TextView textView) {
        CustomDateTimePicker custom = new CustomDateTimePicker(this, new CustomDateTimePicker.ICustomDateTimeListener() {

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
                        if (!serviceId.equalsIgnoreCase("1") || carTypeSpinner.getSelectedItemPosition() > 0) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
                            bookDate = sdf.format(calendarSelected.getTime());
                            bookTime = stf.format(calendarSelected.getTime());
                            bookNow = "0";
                            showConfirmDialog();
                        } else
                            Toast.makeText(getApplicationContext(), "Must select car type and options", Toast.LENGTH_LONG).show();
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
        new android.support.v7.app.AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Booking")
                .setMessage("Do you want to book service at " + Util.getDateTime(bookDate, bookTime) + " ?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        allinAllController.bookAppointment(RESTClient.ID, bookDate, bookTime, serviceId, userLocation.getLongitude() + "", userLocation.getLatitude() + "", serviceId.equalsIgnoreCase("1") ? carTypeSpinner.getSelectedItem().toString() : null, serviceId.equalsIgnoreCase("1") ? driverTypeSpinner.getSelectedItem().toString() : null, bookNow);
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    protected void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Locationnnnnnn ", "Latit " + userLocation.getLatitude() + " long: " + userLocation.getLongitude());
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (RESTClient.PROVIDERS != null) {
            ArrayList<ServiceProvider> surrounding = getSurroundingProviders();
            int numOfProviders = surrounding.size();
            Double[] latitude = new Double[numOfProviders];
            Double[] longitude = new Double[numOfProviders];
            Marker[] markers = new Marker[numOfProviders];
            try {
                for (int i = 0; i < numOfProviders; i++) {
                    latitude[i] = Double.valueOf(surrounding.get(i).getLatitude());
                    longitude[i] = Double.valueOf(surrounding.get(i).getLongitude());
                    markers[i] = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude[i], longitude[i]))
//                                    .title(surrounding.get(i).getAddress())
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
            } // end catch
        }
    }

    private ArrayList<ServiceProvider> getSurroundingProviders() {
        HashMap<ServiceProvider, Float> distances = new HashMap<ServiceProvider, Float>();
        ArrayList<ServiceProvider> providers = new ArrayList<ServiceProvider>();
        for (ServiceProvider prov : RESTClient.PROVIDERS) {
            Location providerLocation = new Location(prov.getServiceProviderName());
            providerLocation.setLatitude(Double.parseDouble(prov.getLatitude()));
            providerLocation.setLongitude(Double.parseDouble(prov.getLongitude()));
            float distance = userLocation.distanceTo(providerLocation);
            Log.e(getString(R.string.tag), "distance: " + distance);
            distances.put(prov, distance);
            if (distance <= SURROUNDING_DISTANCE)
                providers.add(prov);
        }
//        sortDistances(distances);

        return providers;
    }

    /*  private void sortDistances(HashMap<ServiceProvider, Float> distances) {
          final int size = distances.size();
          final List<HashMap.Entry<ServiceProvider, Float>> list = new ArrayList<HashMap.Entry<ServiceProvider, Float>>(size);
          list.addAll(distances.entrySet());
          final ValueComparator<Float> cmp = new ValueComparator<Float>();
          Collections.sort(list, cmp);
          sortedProviders = new ArrayList<ServiceProvider>(size);
          for (int i = 0; i < size; i++) {
              sortedProviders.add(list.get(i).getKey());
          }
      }
  */
    @Override
    public void sendServiceResult(String serviceResult) {
        Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
        if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_book_success)))
            finish();


    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);

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
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (userLocation == null) {
            userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (userLocation != null) {
                mapFragment.getMapAsync(this);
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
                        "upgrade location settings ");

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
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        showCanNotCompleteDialog();
                        break;
                }
                break;
        }
    }

    private void showCanNotCompleteDialog() {
        new android.support.v7.app.AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Location Setting Error")
                .setMessage("Can not complete booking without adjusting the location settings")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        createLocationRequest();
//                        buildLocationSettingsRequest();
                        checkLocationSettings();
                        dialog.dismiss();
                    }
                }).show();
    }
}
