package com.czsm.DD_driver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.model.Data;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrentReqActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    TextView NameTxt, PhonenoTxt, AddressTxt, MapTxt;
    Context context;
    private TrackGPS gps;
    //    private GPSTrackers gps;
    Location mLocation;
    SupportMapFragment mapFrag;

    private GoogleMap mMap;
    double latitu, longitu;
    String address1;
    LatLng laln;
    int markerIcon;
    String UIAVALUE;
    LinearLayout NameLinLay, PhoneLinLay, AddressLinLay;
    RelativeLayout MapRelLay;
    View viewline;
    LocationManager locationmanager;
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    LocationManager mLocationManager;
    android.location.LocationListener mLocationListener;
    FirebaseFirestore db;
    List<Data> datalist = new ArrayList<Data>();
    String userlatt, userlong, userphonenumber, useradress,userlatval,userlongval;
    double Userlat, UserLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_req);
        NameTxt = (TextView) findViewById(R.id.name_txt);
        PhonenoTxt = (TextView) findViewById(R.id.phone_txt);
        AddressTxt = (TextView) findViewById(R.id.add_txt);
        MapTxt = (TextView) findViewById(R.id.map_txt);
        NameLinLay = (LinearLayout) findViewById(R.id.name_lay);
        PhoneLinLay = (LinearLayout) findViewById(R.id.phone_lay);
        AddressLinLay = (LinearLayout) findViewById(R.id.add_lay);

        MapRelLay = (RelativeLayout) findViewById(R.id.map_lay);
        viewline = (View) findViewById(R.id.view2);
        UIAVALUE = PreferencesHelper.getPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID);
        MapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextscr = new Intent(CurrentReqActivity.this, MapScreenActivity.class);
                startActivity(nextscr);
            }
        });
        db = FirebaseFirestore.getInstance();
//popup();
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(CurrentReqActivity.this);
//        mapFrag.getMapAsync(CurrentReqActivity.this);

        com.google.firebase.firestore.Query driverfirst = db.collection("UsersCurrentBooking");

        driverfirst.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocuments().size() < 1) {

                            return;

                        }

                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                            Data data = document.toObject(Data.class);
                            datalist.add(data);
                            userlatt = String.valueOf(datalist.get(0).getCurrentlat());
                            userlong = String.valueOf(datalist.get(0).getCurrentlong());

                             userlatval=userlatt.trim();
                             userlongval=userlong.trim();
                            Userlat = Double.parseDouble(userlatval);
                            UserLong = Double.parseDouble(userlongval);
                            userphonenumber = String.valueOf(datalist.get(0).getPhoneNumber());
                            useradress = String.valueOf(datalist.get(0).getAddress());

                            PhonenoTxt.setText(userphonenumber);
                            AddressTxt.setText(useradress);
//                            Log.e("lattitude", String.valueOf(Userlat));
//                            Log.e("longtude", String.valueOf(UserLong));
//                            Toast.makeText(CurrentReqActivity.this, (CharSequence) AddressTxt, Toast.LENGTH_SHORT).show();

//                                                  Log.e("datalist",datalist.get(0).getLat());
//                                hideProgressDialog();

                        }
//                            hideProgressDialog();


                    }
                });

        MapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapintent= new Intent(CurrentReqActivity.this,MapScreenActivity.class);
                mapintent.putExtra("userlat",userlatt);
                mapintent.putExtra("userlong",userlong);
                startActivity(mapintent);
                finish();

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        Double lat = Userlat;
        Double lng = UserLong;
        Log.e("lattitude", String.valueOf(lat));
                            Log.e("longtude", String.valueOf(lng));

//        Circle circle = Mmap.addCircle(new CircleOptions().center(laln).radius(5000).strokeColor(Color.BLUE).strokeWidth(2.0f));

        LatLng locateme = new LatLng(lat,lng);
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json);
//        Mmap.setMapStyle(style);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
//         final LatLng PERTH = new LatLng(Userlat, UserLong);
//        Marker perth = mMap.addMarker(new MarkerOptions()
//                .position(PERTH)
//                .draggable(true));


        mMap.setMyLocationEnabled(true);
        //   Mmap.addMarker(new MarkerOptions().position(locateme).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locateme,6.5f));
        // map.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);
//        LatLng sydney = new LatLng(Userlat, UserLong);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker in userlocation"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }
    private void popup() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.userreqalert, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(deleteDialogView);
        TextView Acce = (TextView) deleteDialogView.findViewById(R.id.accept_button);
//        TextView cancel = (TextView) deleteDialogView.findViewById(R.id.cancel_button);

        final AlertDialog alertDialog1 = alertDialog.create();
        Acce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NameLinLay.setVisibility(View.VISIBLE);
                PhoneLinLay.setVisibility(View.VISIBLE);
                AddressLinLay.setVisibility(View.VISIBLE);
                MapRelLay.setVisibility(View.VISIBLE);
                viewline.setVisibility(View.VISIBLE);
                MapTxt.setVisibility(View.VISIBLE);

                alertDialog1.dismiss();
            }
        });

//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(CurrentReqActivity.this, "can", Toast.LENGTH_SHORT).show();
////
//                alertDialog1.dismiss();
//            }
//        });


        alertDialog1.setCanceledOnTouchOutside(false);
        try {
            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog1.show();
//        alertDialog1.getWindow().setLayout((int) Utils.convertDpToPixel(228,getActivity()),(int)Utils.convertDpToPixel(220,getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog1.getWindow().getAttributes());
//        lp.height=200dp;
//        lp.width=228;
        lp.gravity = Gravity.CENTER;
//        lp.windowAnimations = R.style.DialogAnimation;
        alertDialog1.getWindow().setAttributes(lp);
    }

    private void setMarkerIcon(String serviceId) {

        markerIcon = R.drawable.lmdriver;

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {

    }


}

