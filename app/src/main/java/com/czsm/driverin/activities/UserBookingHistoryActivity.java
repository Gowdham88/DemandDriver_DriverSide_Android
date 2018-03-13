package com.czsm.driverin.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsm.driverin.R;
import com.czsm.driverin.model.UserBooking;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserBookingHistoryActivity extends AppCompatActivity {
    @InjectView(R.id.user_booking_history_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.user_appointment_details_id_textview)
    TextView idTextView;

    @InjectView(R.id.user_appointment_details_datetime_textview)
    TextView dateTimeTextView;

    @InjectView(R.id.user_appointment_details_drname_textview)
    TextView drNameTextView;

    @InjectView(R.id.user_appointment_details_address_textview)
    TextView addressTextView;

    @InjectView(R.id.user_booking_history_name_textview)
    TextView providerTextView;

    @InjectView(R.id.user_appointment_details_review_linearlayout)
    LinearLayout reviewLayout;

    @InjectView(R.id.user_appointment_details_review_textview)
    TextView reviewTextView;

    private UserBooking booking;
    String drivername,appointmentid,date,driveraddress,review = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings_history);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.app_details);




        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            drivername    = extras.getString("drivername","");
            driveraddress = extras.getString("driveraddress","");
            date          = extras.getString("date","");
            appointmentid = extras.getString("id","");
            review        = extras.getString("review","");

        }

        try {

            idTextView.setText(appointmentid);
            dateTimeTextView.setText(date);
            drNameTextView.setText(drivername);
            addressTextView.setText(driveraddress);

            int reviewstar = Integer.parseInt(review);
                String reviewstr = "";
                for (int i = 0; i < reviewstar; i++) {
                    reviewstr += "*";
                }
            reviewTextView.setText(reviewstr);

        } catch (NullPointerException e){

            e.printStackTrace();
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
}