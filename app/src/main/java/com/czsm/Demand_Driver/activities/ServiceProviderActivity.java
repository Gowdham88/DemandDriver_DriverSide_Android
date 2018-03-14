package com.czsm.Demand_Driver.activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.Service.MyBroadcastReceiver;
import com.czsm.Demand_Driver.adapters.ViewPagerAdapter;
import com.czsm.Demand_Driver.fragments.ProviderBookingsFragment;
import com.czsm.Demand_Driver.fragments.ProviderWalletFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServiceProviderActivity extends AppCompatActivity {

    @BindView(R.id.provider_tablayout)
    TabLayout tabLayout;

    @BindView(R.id.provider_viewpager)
    ViewPager viewPager;

    String providerid = "";

    DatabaseReference db;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);
        ButterKnife.bind(this);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        db                 = FirebaseDatabase.getInstance().getReference();
        preferences        = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String sid         = preferences.getString("providerId","");



        Calendar cal = Calendar.getInstance();

        Intent service = new Intent(getBaseContext(), MyBroadcastReceiver.class);
        cal.add(Calendar.SECOND, 5);
        //TAKE PHOTO EVERY 15 SECONDS
        PendingIntent pintent = PendingIntent.getBroadcast(this, 0, service, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                1000, pintent);


    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(0);
        adapter.addFragment(new ProviderBookingsFragment(), getString(R.string.bookings));
        adapter.addFragment(new ProviderWalletFragment(), getString(R.string.wallet));
        viewPager.setAdapter(adapter);

    }




}
