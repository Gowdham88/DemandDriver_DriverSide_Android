package com.czsm.Demand_Driver.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.model.UserBooking;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserBookingHistoryActivity extends AppCompatActivity {
    @BindView(R.id.user_booking_history_toolbar)
    Toolbar toolbar;

    @BindView(R.id.user_appointment_details_id_textview)
    TextView idTextView;

    @BindView(R.id.user_appointment_details_datetime_textview)
    TextView dateTimeTextView;

    @BindView(R.id.user_appointment_details_drname_textview)
    TextView drNameTextView;

    @BindView(R.id.user_appointment_details_address_textview)
    TextView addressTextView;

    @BindView(R.id.user_booking_history_name_textview)
    TextView providerTextView;

    @BindView(R.id.user_appointment_details_review_linearlayout)
    LinearLayout reviewLayout;

    @BindView(R.id.user_appointment_details_review_textview)
    TextView reviewTextView;

    private UserBooking booking;
    String drivername,appointmentid,date,driveraddress,review = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings_history);
        ButterKnife.bind(this);
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