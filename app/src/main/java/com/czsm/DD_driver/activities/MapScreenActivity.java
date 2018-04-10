package com.czsm.DD_driver.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapScreenActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    android.support.v7.widget.Toolbar toolbar;
    private TrackGPS gps;
    Location mLocation;
    SupportMapFragment mapFrag;
    GoogleMap Mmap;
    double latitu,longitu;
    String address1;
    LatLng laln;
    int markerIcon;
    String Userlat,Userlong;
    Double USERlat,USERlong;
    TextView LogoutTxt;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        mAuth = FirebaseAuth.getInstance();
//        LogoutTxt=(TextView) findViewById(R.id.logout);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.user_service_details_toolbar);
        toolbar.setTitle("MapView");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent mapintent= new Intent(MapScreenActivity.this,ProviderBookingActivity.class);
//                startActivity(mapintent);
//            }
//        });
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(MapScreenActivity.this);
        Intent intent = getIntent();
        if(intent!=null){
            Userlat = intent.getStringExtra("userlat").trim();
            Userlong = intent.getStringExtra("userlong").trim();
            USERlat= Double.valueOf(Userlat);
            USERlong= Double.valueOf(Userlong);
        }

//        LogoutTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showConfirmDialog();
//            }
//        });
//        Log.e("lattitude", String.valueOf(USERlat));
//        Log.e("longtude", String.valueOf(USERlong));

    }
//    private void showConfirmDialog() {
//        new android.support.v7.app.AlertDialog.Builder(MapScreenActivity.this)
//                //set message, title, and icon
//                .setTitle("Sign out")
//                .setMessage("Do you want to sign out?")
//                .setIcon(R.drawable.logo01)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Intent intent = new Intent(MapScreenActivity.this, LoginScreenActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("EXIT", false);
//                        startActivity(intent);
//                        PreferencesHelper.signOut(MapScreenActivity.this);
//                        mAuth.signOut();
//                        finish();
//                    }
//                })
//
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//
//    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mmap=googleMap;
        Mmap.clear();
        Double lat =USERlat;
        Double lng =USERlong;


//        Circle circle = Mmap.addCircle(new CircleOptions().center(laln).radius(5000).strokeColor(Color.BLUE).strokeWidth(2.0f));

        LatLng locateme = new LatLng(lat, lng);
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json);
//        Mmap.setMapStyle(style);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
//         final LatLng PERTH = new LatLng(Userlat, UserLong);
//        Marker perth = mMap.addMarker(new MarkerOptions()
//                .position(PERTH)
//                .draggable(true));


        Mmap.setMyLocationEnabled(true);
        //   Mmap.addMarker(new MarkerOptions().position(locateme).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locateme,6.5f));
        // map.animateCamera(CameraUpdateFactory.zoomIn());
        Mmap.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);
    }
}
