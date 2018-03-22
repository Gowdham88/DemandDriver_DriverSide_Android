package com.czsm.DD_driver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.R;
import com.czsm.DD_driver.view.CustomDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class BookmapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMyLocationClickListener {
    GoogleMap mMap;
    LocationManager locationManager;
    ;
    private GoogleApiClient googleApiClient;
    Toolbar toolbar;
    TextView serviceTextView;
    ImageView serviceImageView;
    LinearLayout driverLayout;
    Spinner driverTypeSpinner;
    AutoCompleteTextView carTypeSpinner;
    LinearLayout tariffLayout;
    TextView bookNowTextview,LocTxt;
    TextView bookLaterTextview;
    Button bookButton;
    private static final int LOCATION_REQUEST_CODE = 101;
    GoogleMap googleMap;
    SupportMapFragment Map;
    CustomDateTimePicker bookLaterDateTimePicker;
    String serviceId = "1";
    String serviceName = "Driver";
    String bookDate, bookTime, bookNow;
    int markerIcon;
    private ArrayList<LatLng> markerPoints;
    BookmapActivity mListener;
    String address1;
    double lat;
    double lng;
    double latitud, longitud, latitu, longitu;
    LatLng laln;
    LatLng latLng;
    Marker mPositionMarker;
    String latlangStr,latStr,langStr;
    Marker marker;
    private TrackGPS gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymap);
        toolbar = (Toolbar) findViewById(R.id.user_service_details_toolbar);
        serviceTextView = (TextView) findViewById(R.id.service_details_service_textview);
        serviceImageView = (ImageView) findViewById(R.id.service_details_service_imageview);
        driverLayout = (LinearLayout) findViewById(R.id.service_details_call_driver_linearlayout);
        driverTypeSpinner = (Spinner) findViewById(R.id.service_details_driver_type_spinner);
        carTypeSpinner = (AutoCompleteTextView) findViewById(R.id.service_details_car_type_spinner);
        tariffLayout = (LinearLayout) findViewById(R.id.service_details_tariff_linearlayout);
        bookNowTextview = (TextView) findViewById(R.id.service_details_book_now_textview);
        bookLaterTextview = (TextView) findViewById(R.id.service_details_book_later_textview);
        bookButton = (Button) findViewById(R.id.service_details_book_button);
        LocTxt=(TextView)findViewById(R.id.location_txt);

        toolbar.setTitle("Current Booking");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

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
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map));
        gps = new TrackGPS(BookmapActivity.this);
        try {
            if(gps.canGetLocation()){
                Double lat =gps .getLatitude();
                Double lng =  gps.getLongitude();
                List<Address> addresses = null;
                try {
                    Geocoder geo = new Geocoder(BookmapActivity.this.getApplicationContext(), Locale.getDefault());
                    addresses = geo.getFromLocation(lat, lng, 1);
                    if (addresses.isEmpty()) {
                    }
                    else {
                        if (addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            address1=(address + "," + city + "," + state + "," + country + "," + postalCode);

                            //                         Eaddress.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                gps.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        markerIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                markerPoints.clear();
//
//                markerPoints.add(new LatLng(23.049543, 72.517195));
//                markerPoints.add(new LatLng(23.048457, 72.516787));
//                markerPoints.add(new LatLng(23.048989, 72.516973));
//                markerPoints.add(new LatLng(23.048263, 72.516667));
//                markerPoints.add(new LatLng(23.047409, 72.516281));
//                markerPoints.add(new LatLng(23.046219, 72.515696));
//
//                bitmap Icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo01);
//                setAnimation(mMap,markerPoints,Icon);
//            }
//        });

//        googleMap = supportMapFragment.getMap();
//        googleMap.setMyLocationEnabled(true);
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String bestProvider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(bestProvider);
//        if (location != null) {
//            onLocationChanged(location);
//        }
//        locationManager.requestLocationUpdates(bestProvider, 20000, 0, (LocationListener) this);

    }

    private void setMarkerIcon(String serviceId) {

        markerIcon = R.drawable.lmdriver;

    }

    @Override
    public void onLocationChanged(Location location) {
//        TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        double lattitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(lattitude, longitude);



//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng).zoom(14).build();
//
//        mMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
//                .position(latLng)
//                .zIndex(20));
//
//        mMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

        if (location == null)
            return;


        if (mPositionMarker == null) {

            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bitmap))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(location.getLatitude(), location
                                    .getLongitude())));
        }

        animateMarker(mPositionMarker, location); // Helper method for smooth
        // animation

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location
                .getLatitude(), location.getLongitude())));

//        googleMap.addMarker(new MarkerOptions().position(latLng));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }

    

   
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationandAddToMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {


                if(gps.canGetLocation()) {
                    Double lat = gps.getLatitude();
                    Double lng = gps.getLongitude();
                    LatLng locateme = new LatLng(lat, lng);
//                    handlenewlocation(locateme);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"SORRY WE COULDN`T TRACK YOUR LOCATION",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                laln = cameraPosition.target;
                mMap.clear();

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(laln.latitude);
                    mLocation.setLongitude(laln.longitude);
                    List<Address> addresses = null;
                    Geocoder geo = new Geocoder(BookmapActivity.this.getApplicationContext(), Locale.getDefault());
                    addresses = geo.getFromLocation(laln.latitude, laln.longitude, 1);

                    if (addresses.isEmpty()) {

                    }
                    else {
                        if (addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            address1=(address + "," + city + "," + state + "," + country + "," + postalCode);

                            //                         Eaddress.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                    LocTxt.setText(address1);
////                    map_loc = "http://maps.google.com/maps?q=loc:" + laln.latitude + "," + laln.longitude + "1";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void handlenewlocation(final LatLng laln)
    {
        mMap.clear();
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(SosMap.this, R.raw.style_json);
//        mMap.setMapStyle(style);
        //  Mmap.addMarker(new MarkerOptions().position(laln).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(laln,6.5f));
        // map.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);
        latitu=laln.latitude;
        longitu=laln.longitude;



    }

    @Override
    public void onConnected(Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void checkLocationandAddToMap() {
//Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//Requesting the Location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

//Fetching the last known location using the Fus
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

//MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here");

//Adding the created the marker on the map
        
        mMap.addMarker(markerOptions);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
         location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Toast.makeText(this, location.getLatitude() + " " + location.getLongitude() + "", Toast.LENGTH_LONG).show();

//            LatLng position = marker.getPosition(); //
//            Toast.makeText(
//                    BookmapActivity.this,
//                    "Lat " + position.latitude + " "
//                            + "Long " + position.longitude,
//                    Toast.LENGTH_LONG).show();
             latStr= String.valueOf(location.getLatitude());
            langStr=String.valueOf(location.getLongitude());
            latlangStr=latStr+","+langStr;
//             LocTxt.setText(latlangStr);
//            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//
//                @Override
//                public void onMarkerDragStart(Marker marker) {
//                    // TODO Auto-generated method stub
//                    // Here your code
//                    Toast.makeText(BookmapActivity.this, "Dragging Start",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onMarkerDragEnd(Marker marker) {
//                    // TODO Auto-generated method stub
//                    Toast.makeText(
//                            BookmapActivity.this,
//                            "Lat " + mMap.getMyLocation().getLatitude() + " "
//                                    + "Long " + mMap.getMyLocation().getLongitude(),
//                            Toast.LENGTH_LONG).show();
//                    System.out.println("yalla b2a "
//                            + mMap.getMyLocation().getLatitude());
//                }
//
//                @Override
//                public void onMarkerDrag(Marker marker) {
//                    // TODO Auto-generated method stub
//                    // Toast.makeText(MainActivity.this, "Dragging",
//                    // Toast.LENGTH_SHORT).show();
//                    System.out.println("Draagging");
//                }
//            });

        }
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(location.getLatitude(), location.getLongitude()),6f));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }


}