package com.czsm.DD_driver.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.Firebasemodel.AppointmentList;
import com.czsm.DD_driver.Firebasemodel.ServiceproviderList;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.helper.RESTClient;
import com.czsm.DD_driver.helper.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserOngoingBookingActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.user_appointment_details_id_textview)
    TextView idTextView;

    @BindView(R.id.user_appointment_details_datetime_textview)
    TextView dateTimeTextView;

    @BindView(R.id.user_appointment_details_drname_textview)
    TextView drNameTextView;

    @BindView(R.id.user_booking_history_name_textview)
    TextView providerTextView;

    @BindView(R.id.user_appointment_details_address_textview)
    TextView addressTextView;

    @BindView(R.id.user_appointment_details_stts_textview)
    TextView statusTextView;

    @BindView(R.id.user_appointment_details_complete_button)
    Button completeButton;

    @BindView(R.id.user_appointment_details_cancel_button)
    Button cancelButton;

    DatabaseReference db;
    SharedPreferences sharedPreferences;
    String drivername,appointmentid,status,date,driveraddress,id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ongoing_booking);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.app_details);

        db = FirebaseDatabase.getInstance().getReference();




        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            drivername    = extras.getString("drivername","");
            status        = extras.getString("status","");
            driveraddress = extras.getString("driveraddress","");
            date          = extras.getString("date","");
            appointmentid = extras.getString("userid","");
            id            = extras.getString("providerid","");

        }

        try {

            idTextView.setText(appointmentid);
            dateTimeTextView.setText(date);
            drNameTextView.setText(drivername);
            addressTextView.setText(driveraddress);
            statusTextView.setText(status);

            if (status.equals("Confirmed")) {

                completeButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);

            } else {

                completeButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                completeButton.setOnClickListener(null);
                cancelButton.setOnClickListener(null);

            }


        } catch (NullPointerException e){

            e.printStackTrace();
        }

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showConformDialog("Completed","Are you sure you want complete this appointment.");

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showConformDialog("Cancelled","Are you sure you want cancel this appointment.");

            }
        });



    }

    private void showConformDialog(final String type,final String message) {
        new android.support.v7.app.AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Complete")
                .setMessage(message)
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        ValueEventListener maplistner = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot child : dataSnapshot.getChildren()) {


                                    child.getRef().child("status").setValue(type);

                                    AppointmentList appointmentList = child.getValue(AppointmentList.class);

                                    if(appointmentList.getStatus().equals(type)) {

                                        Toast.makeText(getApplicationContext(), "Your appointment has been" + type, Toast.LENGTH_SHORT).show();

                                    }

                                    if(appointmentList.getStatus().equals("Completed")){

                                        showRatingDialog(child);
                                    }

                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.e("loadPost:onCancelled", databaseError.toException().toString());
                            }
                        };

                        db.child("AppointmentList").orderByKey().equalTo(appointmentid).addListenerForSingleValueEvent(maplistner);

                        ValueEventListener listner = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                    ServiceproviderList data = child.getValue(ServiceproviderList.class);

                                    if(data.getStatus().equals("onduty"))

                                        child.getRef().child("status").setValue("free");

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.e("loadPost:onCancelled", databaseError.toException().toString());
                            }
                        };

                        db.child("ServiceproviderList").orderByKey().equalTo(id).addValueEventListener(listner);






                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).show();
    }

    public void showRatingDialog(final DataSnapshot child) {
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_review_booking, null);

        new AlertDialog.Builder(this).setIcon(android.R.drawable.btn_star_big_on).setTitle("Review")
                .setView(dialogView)
                .setMessage("Appreciate giving feedback about this Appointment")
                .setCancelable(false)
                .setPositiveButton("Rate",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.dialog_review_booking_ratingbar);
                                String review = ratingBar.getProgress() + "";
                                child.getRef().child("userreview").setValue(review);
                                dialog.dismiss();
                                finish();
                            }
                        })

                        // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                child.getRef().child("userreview").setValue("0");
                                finish();
                                dialog.cancel();
                            }
                        }).setCancelable(false).show();
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
    public void sendServiceResult(String serviceResult) {

    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}