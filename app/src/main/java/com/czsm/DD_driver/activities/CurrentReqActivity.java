package com.czsm.DD_driver.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CurrentReqActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    TextView NameTxt,PhonenoTxt,AddressTxt,MapTxt;
    Context context;
    private TrackGPS gps;
    Location mLocation;
    SupportMapFragment mapFrag;
    GoogleMap Mmap;
    double latitu,longitu;
    String address1;
    LatLng laln;
    int markerIcon;
    String UIAVALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_req);
        NameTxt=(TextView)findViewById(R.id.name_txt);
        PhonenoTxt=(TextView)findViewById(R.id.phone_txt);
        AddressTxt=(TextView)findViewById(R.id.add_txt);
        MapTxt=(TextView)findViewById(R.id.map_txt);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(CurrentReqActivity.this);

        gps = new TrackGPS(CurrentReqActivity.this);
        try {
            if(gps.canGetLocation()){
                Double lat =gps .getLatitude();
                Double lng =  gps.getLongitude();
                List<Address> addresses = null;
                try {
                    Geocoder geo = new Geocoder(CurrentReqActivity.this, Locale.getDefault());
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

    }

    private void setMarkerIcon(String serviceId) {

        markerIcon = R.drawable.lmdriver;

    }
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Mmap.clear();
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

}

