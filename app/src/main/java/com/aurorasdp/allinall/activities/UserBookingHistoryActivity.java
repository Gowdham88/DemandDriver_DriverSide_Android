package com.aurorasdp.allinall.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.model.UserBooking;

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
            int index = extras.getInt("bookingIndex");
            booking = RESTClient.USER_BOOKINGS_HISTORY.get(index);
            if (booking != null) {
                idTextView.setText(booking.getBookingCode());
                dateTimeTextView.setText(booking.getDateTime());
                drNameTextView.setText(booking.getProviderName());
                addressTextView.setText(booking.getAddress());
                providerTextView.setText(booking.getService() + " " + "name");
                int review = Integer.parseInt(booking.getReview());
                String reviewstr = "";
                for (int i = 0; i < review; i++) {
                    reviewstr += "*";
                }
                reviewTextView.setText(reviewstr);
            }
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