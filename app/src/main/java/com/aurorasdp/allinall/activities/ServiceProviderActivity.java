package com.aurorasdp.allinall.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.adapters.ViewPagerAdapter;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.fragments.ProviderBookingsFragment;
import com.aurorasdp.allinall.fragments.ProviderWalletFragment;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ServiceProviderActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {
    @InjectView(R.id.provider_tablayout)
    TabLayout tabLayout;

    @InjectView(R.id.provider_viewpager)
    ViewPager viewPager;

    private AllinAllController allinAllController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);
        ButterKnife.inject(this);
//        Log.e("AllinAll", "Extras "+ getIntent().getExtras());
        allinAllController = new AllinAllController(this, this);
        allinAllController.getPendingAppointments(RESTClient.ID);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(0);
        adapter.addFragment(new ProviderBookingsFragment(), getString(R.string.bookings));
        adapter.addFragment(new ProviderWalletFragment(), getString(R.string.wallet));
        viewPager.setAdapter(adapter);

    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_pending_success))) {
            if (RESTClient.PENDING_MESSAGES != null && !RESTClient.PENDING_MESSAGES.equalsIgnoreCase("")) {
                String[] messagesList = TextUtils.split(RESTClient.PENDING_MESSAGES, ",");
                final String[] idsList = TextUtils.split(RESTClient.PENDING_IDS, ",");
                for (int i = 0; i < idsList.length; i++) {
                    final int finalI = i;
                    new android.support.v7.app.AlertDialog.Builder(this)
                            //set message, title, and icon
                            .setTitle("New Booking: ")
                            .setMessage(messagesList[i])
                            .setIcon(R.drawable.ic_launcher)
                            .setCancelable(false)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    allinAllController.confirmAppointment(idsList[finalI]);
                                }
                            })
                            .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    allinAllController.rejectAppointment(idsList[finalI]);
                                }
                            }).show();
                }
            }
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }

//    @Override
//    public void onBackPressed() {
//        getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        finish();
//    }
}
