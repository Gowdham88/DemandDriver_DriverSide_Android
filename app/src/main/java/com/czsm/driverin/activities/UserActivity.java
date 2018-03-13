package com.czsm.driverin.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.czsm.driverin.R;
import com.czsm.driverin.adapters.ViewPagerAdapter;
import com.czsm.driverin.helper.RESTClient;
import com.czsm.driverin.receiver.NotificationBroadcastReceiver;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserActivity extends AppCompatActivity {

    @InjectView(R.id.user_tablayout)
    TabLayout tabLayout;

    @InjectView(R.id.user_viewpager)
    ViewPager viewPager;

    SharedPreferences sharedPreferences;
    private NotificationBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.inject(this);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        String fromPush = null;
        String message = null;
        if (extras != null) {

            fromPush = extras.getString("FROM_PUSH");
        }
        if (fromPush != null) {

            RESTClient.ID = sharedPreferences.getString("userId", "");

        }


        message = sharedPreferences.getString("user_message", "");
        if (!message.equalsIgnoreCase("")) {
            showRejectDialog(message);
            sharedPreferences.edit().remove("user_message").apply();
        }
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        if (fromPush != null && fromPush.equalsIgnoreCase("1"))
            viewPager.setCurrentItem(2);

        mReceiver = new NotificationBroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Bundle extra = intent.getExtras();
                if (extra != null && extra.getString("user_message") != null) {
                    showRejectDialog(extra.getString("user_message"));
                }
            }
        };
        registerReceiver(mReceiver, new IntentFilter(NotificationBroadcastReceiver.NOTIFICATION_RECEIVED));
    }

    private void showRejectDialog(String userMessage) {
        new android.support.v7.app.AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Booking")
                .setMessage(userMessage)
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
//        adapter.addFragment(new BookServiceMapActivity(), getString(R.string.services));
//        adapter.addFragment(new UserSupportFragment(), getString(R.string.support));
//        adapter.addFragment(new OngoingAppointmentsFragment(), getString(R.string.ongoing_app));
//        adapter.addFragment(new UserHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
