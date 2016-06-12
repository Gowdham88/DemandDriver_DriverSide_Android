package com.aurorasdp.allinall.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.adapters.ViewPagerAdapter;
import com.aurorasdp.allinall.fragments.OngoingAppointmentsFragment;
import com.aurorasdp.allinall.fragments.ProviderBookingsFragment;
import com.aurorasdp.allinall.fragments.ProviderWalletFragment;
import com.aurorasdp.allinall.fragments.UserHistoryFragment;
import com.aurorasdp.allinall.fragments.UserServicesFragment;
import com.aurorasdp.allinall.fragments.UserSupportFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserActivity extends AppCompatActivity {

    @InjectView(R.id.user_tablayout)
    TabLayout tabLayout;

    @InjectView(R.id.user_viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.inject(this);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        adapter.addFragment(new UserServicesFragment(), getString(R.string.services));
        adapter.addFragment(new UserSupportFragment(), getString(R.string.support));
        adapter.addFragment(new OngoingAppointmentsFragment(), getString(R.string.ongoing_app));
        adapter.addFragment(new UserHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);

    }

}
