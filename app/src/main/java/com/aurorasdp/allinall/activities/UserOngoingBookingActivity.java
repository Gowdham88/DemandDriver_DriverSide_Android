package com.aurorasdp.allinall.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.model.UserBooking;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserOngoingBookingActivity extends AppCompatActivity {
    @InjectView(R.id.user_ongoing_booking_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.user_appointment_details_id_textview)
    TextView idTextView;

    @InjectView(R.id.user_appointment_details_datetime_textview)
    TextView dateTimeTextView;

    @InjectView(R.id.user_appointment_details_drname_textview)
    TextView drNameTextView;

    @InjectView(R.id.user_booking_history_name_textview)
    TextView providerTextView;

    @InjectView(R.id.user_appointment_details_address_textview)
    TextView addressTextView;

    @InjectView(R.id.user_appointment_details_stts_textview)
    TextView statusTextView;

    public static UserBooking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ongoing_booking);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.app_details);
        if (booking != null) {
            idTextView.setText(booking.getBookingCode());
            dateTimeTextView.setText(booking.getDateTime());
            providerTextView.setText(booking.getService() + " " + "name");
            drNameTextView.setText(booking.getProviderName());
            addressTextView.setText(booking.getAddress());
            statusTextView.setText(booking.getStatus());
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