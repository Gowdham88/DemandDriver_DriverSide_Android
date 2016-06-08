package com.aurorasdp.allinall.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

    @InjectView(R.id.provider_book_contact_textview)
    TextView contactTextView;

    @InjectView(R.id.provider_book_address_textview)
    TextView addressTextView;

    @InjectView(R.id.provider_book_service_type_textview)
    TextView serviceTextView;

    @InjectView(R.id.provider_book_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_book);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.appointment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int bookingIndex = extras.getInt("bookingIndex");
            if (bookingIndex >= 0) {
                ProviderBooking booking = RESTClient.PROVIDER_BOOKINGS.get(bookingIndex);
                nameTextView.setText(booking.getUserName());
                dateTimeTextView.setText(booking.getDateTime());
                contactTextView.setText(booking.getUserPhone());
                addressTextView.setText(booking.getUserAddress());
                serviceTextView.setText(booking.getService());
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
