package com.czsm.DD_driver.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.Firebasemodel.ServiceproviderList;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.Service.CapPhoto;
import com.czsm.DD_driver.controller.AllinAllController;
import com.czsm.DD_driver.helper.RESTClient;
import com.czsm.DD_driver.helper.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProviderBookingActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {

    @BindView(R.id.provider_book_name_textview)
    TextView nameTextView;

    @BindView(R.id.provider_book_datetime_textview)
    TextView dateTimeTextView;

    @BindView(R.id.provider_book_contact_textview)
    TextView contactTextView;

    @BindView(R.id.provider_book_address_textview)
    TextView addressTextView;

    @BindView(R.id.provider_book_cancel_button)
    Button cancelButton;

    @BindView(R.id.provider_book_complete_button)
    Button completeButton;

    @BindView(R.id.provider_book_toolbar)
    Toolbar toolbar;

    private AllinAllController allinAllController;
    int bookingIndex;
    String username,usermobile,useraddress,date,userid,id = "",UserLat,UserLong;
    SharedPreferences sharedPreferences;
    DatabaseReference db;
    ImageView forwardImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_book);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.appointment);

        db = FirebaseDatabase.getInstance().getReference();
        forwardImg=(ImageView)findViewById(R.id.add_arrow);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        id                = sharedPreferences.getString("providerId","");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            username    = extras.getString("lat","");
            usermobile  = extras.getString("phonenumber","");
            useraddress = extras.getString("address","");
            date        = extras.getString("datatime","");
            UserLat        = extras.getString("userlats","");
            UserLong        = extras.getString("userlongs","");
//            userid      = extras.getString("userid","");

        }

        try {

            nameTextView.setText(username);
            dateTimeTextView.setText(date);
            contactTextView.setText(usermobile);
            addressTextView.setText(useraddress);


        }catch (NullPointerException e){

            e.printStackTrace();
        }
        forwardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addintent=new Intent(ProviderBookingActivity.this, MapScreenActivity.class);
                addintent.putExtra("userlat",UserLat);
                addintent.putExtra("userlong",UserLong);
                startActivity(addintent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                 showConfirmDialog("Cancelled","Are you sure you want to cancel this appointment.");
//

            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showConfirmDialog("Completed","Are you sure you want to cancel this appointment.");

            }
        });


    }

//    private void showConfirmDialog(final String type,final String message) {
//        new android.support.v7.app.AlertDialog.Builder(this)
//                //set message, title, and icon
//                .setTitle("Cancel")
//                .setMessage(message)
//                .setIcon(R.drawable.ic_launcher)
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//
//                        ValueEventListener maplistner = new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                                    child.getRef().child("status").setValue(type);
//
//                                    Intent service = new Intent(getApplicationContext(), CapPhoto.class);
//                                    stopService(service);
//
//
//                                }
//
//                                Toast.makeText(getApplicationContext(),"Your appointment has been "+type,Toast.LENGTH_SHORT).show();
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                                Log.e("loadPost:onCancelled", databaseError.toException().toString());
//                            }
//                        };
//
//                        db.child("AppointmentList").orderByKey().equalTo(userid).addListenerForSingleValueEvent(maplistner);
//
//                        ValueEventListener listner = new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                                    ServiceproviderList data = child.getValue(ServiceproviderList.class);
//
//                                    if(data.getStatus().equals("onduty"))
//
//                                        child.getRef().child("status").setValue("free");
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                                Log.e("loadPost:onCancelled", databaseError.toException().toString());
//                            }
//                        };
//
//                        db.child("ServiceproviderList").orderByKey().equalTo(id).addValueEventListener(listner);
//
//
//                    }
//                })
//
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//
//                    }
//                }).show();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;

        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendServiceResult(String serviceResult) {

    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}
