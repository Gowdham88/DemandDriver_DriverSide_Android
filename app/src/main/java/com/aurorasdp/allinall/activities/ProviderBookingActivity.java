package com.aurorasdp.allinall.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.model.ProviderBooking;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProviderBookingActivity extends AppCompatActivity {

    @InjectView(R.id.provider_book_name_textview)
    TextView nameTextView;

    @InjectView(R.id.provider_book_datetime_textview)
    TextView dateTimeTextView;

    @InjectView(R.id.provider_book_age_textview)
    TextView ageTextView;

    @InjectView(R.id.provider_book_service_type_textview)
    TextView serviceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_book);
        ButterKnife.inject(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int bookingIndex = extras.getInt("bookingIndex");
            if (bookingIndex >= 0) {
                ProviderBooking booking = RESTClient.PROVIDER_BOOKINGS.get(bookingIndex);
                nameTextView.setText(booking.getUserName());
                dateTimeTextView.setText(booking.getDateTime());
                ageTextView.setText(booking.getAge());
                serviceTextView.setText(booking.getService());
            }
        }
    }
}
