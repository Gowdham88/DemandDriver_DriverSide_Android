package com.aurorasdp.allinall.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.model.ServiceProvider;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookServiceMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
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

    String serviceId, serviceName;
    int serviceImageId;

    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int SURROUNDING_DISTANCE = 30000000;
    protected static final String TAG = "GettingUserLocation";
    private Location userLocation;
    HashMap<ServiceProvider, Float> sortedDistances;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service_map);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.book_appointment);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            serviceId = extras.getString("serviceId");
            serviceName = extras.getString("serviceName");
            serviceImageId = extras.getInt("serviceImageId");
            if (!serviceId.equalsIgnoreCase("1")) {
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
        mMap = googleMap;
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
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.callus))
                    );
                }
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
            distances.put(prov, distance);
            if (distance <= SURROUNDING_DISTANCE)
                providers.add(prov);
        }
        sortDistances(distances);

        return providers;
    }

    private void sortDistances(HashMap<ServiceProvider, Float> distances) {
        final int size = distances.size();
        final List<HashMap.Entry<ServiceProvider, Float>> list = new ArrayList<HashMap.Entry<ServiceProvider, Float>>(size);
        list.addAll(distances.entrySet());
        final ValueComparator<Float> cmp = new ValueComparator<Float>();
        Collections.sort(list, cmp);
        sortedDistances = new HashMap<ServiceProvider, Float>(size);
        for (int i = 0; i < size; i++) {
            sortedDistances.put(list.get(i).getKey(), list.get(i).getValue());
        }
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
//                .setIcon(R.drawable.ic_launcher)
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
