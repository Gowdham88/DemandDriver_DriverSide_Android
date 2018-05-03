package com.czsm.DD_driver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.czsm.DD_driver.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyActivity extends AppCompatActivity {
    @BindView(R.id.provider_book_toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_righ);

//                Intent mapintent= new Intent(ProviderBookingActivity.this,ServiceProviderActivity.class);
//                startActivity(mapintent);
            }
        });
        setTitle(R.string.privacy);
    }
}
