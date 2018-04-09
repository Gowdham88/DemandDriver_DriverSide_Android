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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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

public class CurrentReqActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener{
    TextView NameTxt, PhonenoTxt, AddressTxt, MapTxt;
    Context context;
    private TrackGPS gps;
//    private GPSTrackers gps;
    Location mLocation;
    SupportMapFragment mapFrag;
    GoogleMap Mmap;
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
        db= FirebaseFirestore.getInstance();

    mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(CurrentReqActivity.this);

        com.google.firebase.firestore.Query driverfirst = db.collection("UsersBookingRequest");

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
                            String userlat= String.valueOf(datalist.get(0).getLat());
                            String userlong= String.valueOf(datalist.get(0).getLongitude());
                            String userphonenumber= String.valueOf(datalist.get(0).getPhoneNumber());
//                                                  String latt= String.valueOf(datalist.get(0).getLat());
                            Toast.makeText(CurrentReqActivity.this, userlat, Toast.LENGTH_SHORT).show();
                            Toast.makeText(CurrentReqActivity.this, userlong, Toast.LENGTH_SHORT).show();
                            Toast.makeText(CurrentReqActivity.this, userphonenumber, Toast.LENGTH_SHORT).show();

//                                                  Log.e("datalist",datalist.get(0).getLat());
//                                hideProgressDialog();

                        }
//                            hideProgressDialog();


                    }
                });

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference citiesRef = db.collection("cities");
//        Query query = citiesRef.orderBy("name").limit(3);
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
////                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//
//                if (snapshot != null && snapshot.exists()) {
////                    Log.d(TAG, "Current data: " + snapshot.getData());
//                } else {
////                    Log.d(TAG, "Current data: null");
//                }
//            }
//        });
//            final DocumentReference docRef = db.collection("cities").document("SF");
//            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                    @Nullable FirebaseFirestoreException e) {
//                    if (e != null) {
//                        popup();
////                    Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//
//                    if (snapshot != null && snapshot.exists()) {
////                    Log.d(TAG, "Current data: " + snapshot.getData());
//                    } else {
////                    Log.d(TAG, "Current data: null");
//                    }
//                }
//            });
//// The number of children will always be equal to 'count' since the value of
//// the dataSnapshot here will include every child_added event triggered before this point.
////        ref.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                long numChildren = dataSnapshot.getChildrenCount();
////                System.out.println(count.get() + " == " + numChildren);
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {}
////        });
            gps = new TrackGPS(CurrentReqActivity.this);
            try {
                if (gps.canGetLocation()) {
                    Double lat = gps.getLatitude();
                    Double lng = gps.getLongitude();
                    List<Address> addresses = null;
                    try {
                        Geocoder geo = new Geocoder(CurrentReqActivity.this, Locale.getDefault());
                        addresses = geo.getFromLocation(lat, lng, 1);
                        if (addresses.isEmpty()) {
                        } else {
                            if (addresses.size() > 0) {
                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
                                address1 = (address + "," + city + "," + state + "," + country + "," + postalCode);

                                //                         Eaddress.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                                //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    gps.showSettingsAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    private void popup() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.userreqalert, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(deleteDialogView);
        TextView Acce = (TextView)deleteDialogView.findViewById(R.id.accept_button);
        TextView cancel = (TextView)deleteDialogView.findViewById(R.id.cancel_button);

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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(CurrentReqActivity.this, "can", Toast.LENGTH_SHORT).show();
//
                alertDialog1.dismiss();
            }
        });


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
    public void onLocationChanged(Location location) {
        mLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Mmap.clear();

//        Log.e("lat", String.valueOf(location.getLatitude()));
//        Log.e("long", String.valueOf(location.getLongitude()));
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
//                SosMap.this, R.raw.style_json);
//        Mmap.setMapStyle(style);
        //    Mmap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_markf)));
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(laln,6.5f));
        Mmap.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);
        Mmap.setMaxZoomPreference(14.5f);
        Mmap.setMinZoomPreference(6.5f);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mmap=googleMap;
        Mmap.clear();
        Double lat = gps.getLatitude();
        Double lng = gps.getLongitude();
        String  lattitude= String.valueOf(lat);
        String  longtude= String.valueOf(lng);
        Log.e("lattitude",lattitude);
        Log.e("longtude",longtude);

        LatLng locateme = new LatLng(lat, lng);
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json);
//        Mmap.setMapStyle(style);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Mmap.setMyLocationEnabled(true);
        //   Mmap.addMarker(new MarkerOptions().position(locateme).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locateme,6.5f));
        // map.animateCamera(CameraUpdateFactory.zoomIn());
        Mmap.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);
        //      Mmap.addMarker(new MarkerOptions().position(new LatLng(12.978424,80.219333)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        //    Mmap.addMarker(new MarkerOptions().position(new LatLng(13.031522,80.201531)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        Mmap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if(gps.canGetLocation()) {
                    Double lat = gps.getLatitude();
                    Double lng = gps.getLongitude();

                    LatLng locateme = new LatLng(lat, lng);
                    String  lattitude= String.valueOf(locateme);
//
                    Log.e("locateme",lattitude);
//
                    handlenewlocation(locateme);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"SORRY WE COULDN`T TRACK YOUR LOCATION",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        Mmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                Toast.makeText(MapActivity.this, "haaiohb", Toast.LENGTH_SHORT).show();
                laln = cameraPosition.target;
                Mmap.clear();

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(laln.latitude);
                    mLocation.setLongitude(laln.longitude);
                    String Strlat= String.valueOf(laln.latitude);
                    String Strlong= String.valueOf(laln.longitude);
                    Log.e("Strlat",Strlat);
                    Log.e("Strlong",Strlong);
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();


//                    DocumentReference washingtonRef = db.collection("Users").document(UIAVALUE);
//                    HashMap<String, Object> city1 = new HashMap<>();
//                    city1.put("lat", Strlat);
//                    city1.put("long",Strlong);
//
//// Set the "isCapital" field of the city 'DC'
//                    washingtonRef
//                            .update(city1)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
////                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
////                                    Log.w(TAG, "Error updating document", e);
//                                }
//                            });


                    List<Address> addresses;
                    Geocoder geo = new Geocoder(CurrentReqActivity.this.getApplicationContext(), Locale.getDefault());
                    addresses = geo.getFromLocation(laln.latitude, laln.longitude, 1);
                    if (addresses.isEmpty()) {


                    }
                    else {
                        if (addresses.size() > 0) {
                            final String lattitude= String.valueOf(addresses.get(0).getLatitude());
                            final String longitude= String.valueOf(addresses.get(0).getLongitude());
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            address1=(address + "," + city + "," + state + "," + country + "," + postalCode);


//                            DocumentReference documentReference=db.collection("Drivers").document(UIAVALUE);
//                            HashMap<String,Object> updates=new HashMap<>();
//                            updates.put("lat",lattitude);
//                            updates.put("long",longitude);
//                            documentReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    Log.e("lat",lattitude);
//                                    Log.e("long",longitude);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//
//                                }
//                            });


                            //                         Eaddress.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                    Toast.makeText(CurrentReqActivity.this, address1, Toast.LENGTH_SHORT).show();
//                    changelocation.setText(address1);
//                    map_loc = "http://maps.google.com/maps?q=loc:" + laln.latitude + "," + laln.longitude + "1";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handlenewlocation(final LatLng laln)
    {
        Mmap.clear();
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(SosMap.this, R.raw.style_json);
//        Mmap.setMapStyle(style);
        //  Mmap.addMarker(new MarkerOptions().position(laln).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(laln,6.5f));
        // map.animateCamera(CameraUpdateFactory.zoomIn());
        Mmap.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);
        Mmap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(laln.latitude,laln.longitude), 13));
        latitu=laln.latitude;
        longitu=laln.longitude;

//        Log.e("newlat", String.valueOf(latitu));
//        Log.e("newlong", String.valueOf(longitu));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

