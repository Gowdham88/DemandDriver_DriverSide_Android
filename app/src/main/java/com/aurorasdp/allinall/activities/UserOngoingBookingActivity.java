package com.aurorasdp.allinall.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.model.UserBooking;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserOngoingBookingActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {
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

    @InjectView(R.id.user_appointment_details_complete_button)
    Button completeButton;

    public static int bookingIndex = -1;
    private UserBooking booking;
    private AllinAllController allinAllController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ongoing_booking);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.app_details);
        allinAllController = new AllinAllController(this, this);
        if (bookingIndex >= 0)
            booking = RESTClient.ONGOING_BOOKINGS.get(bookingIndex);
        if (booking != null) {
            idTextView.setText(booking.getBookingCode());
            dateTimeTextView.setText(booking.getDateTime());
            providerTextView.setText(booking.getService() + " " + "name");
            drNameTextView.setText(booking.getProviderName());
            addressTextView.setText(booking.getAddress());
            statusTextView.setText(booking.getStatus());
        }
        if (booking.getStatus().equalsIgnoreCase(getString(R.string.confirmed_status)))
            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allinAllController.completeBooking(booking.getBookingId());
                }
            });
    }

    public void showRatingDialog() {
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
                                allinAllController.reviewAppointment(booking.getBookingId(), review);
                                booking.setReview(review);
                                RESTClient.USER_BOOKINGS_HISTORY.add((booking));
                                finish();
                                dialog.dismiss();
                            }
                        })

                        // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RESTClient.USER_BOOKINGS_HISTORY.add((booking));
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
        if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_complete_success))) {
            showRatingDialog();
            RESTClient.ONGOING_BOOKINGS.remove(bookingIndex);
        }
    }

    @Override
    public void requestFailed() {

    }
}