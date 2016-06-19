package com.aurorasdp.allinall.activities;


import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.adapters.ViewPagerAdapter;
import com.aurorasdp.allinall.fragments.ProviderBookingsFragment;
import com.aurorasdp.allinall.fragments.ProviderWalletFragment;
import com.aurorasdp.allinall.helper.RESTClient;


import butterknife.ButterKnife;
import butterknife.InjectView;

public class ServiceProviderActivity extends AppCompatActivity {
    @InjectView(R.id.provider_tablayout)
    TabLayout tabLayout;

    @InjectView(R.id.provider_viewpager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);
        ButterKnife.inject(this);
//        Log.e("AllinAll", "Extras "+ getIntent().getExtras());
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




}
