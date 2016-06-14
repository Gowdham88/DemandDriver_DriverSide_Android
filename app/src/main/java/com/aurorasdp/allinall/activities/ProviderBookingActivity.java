package com.aurorasdp.allinall.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.ProviderBooking;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProviderBookingActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {

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

    @InjectView(R.id.provider_book_cancel_button)
    Button cancelButton;

    @InjectView(R.id.provider_book_toolbar)
    Toolbar toolbar;

    private AllinAllController allinAllController;
    int bookingIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_book);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.appointment);

        allinAllController = new AllinAllController(this, this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bookingIndex = extras.getInt("bookingIndex");
            if (bookingIndex >= 0) {
                final ProviderBooking booking = RESTClient.PROVIDER_BOOKINGS.get(bookingIndex);
                nameTextView.setText(booking.getUserName());
                dateTimeTextView.setText(booking.getDateTime());
                contactTextView.setText(booking.getUserPhone());
                addressTextView.setText(booking.getUserAddress());
                serviceTextView.setText(booking.getService());
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allinAllController.cancelAppointment(booking.getBookingId());
                    }
                });
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

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_cancel_success))) {
            RESTClient.PROVIDER_BOOKINGS.remove(bookingIndex);
            finish();
        } else
            Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}
