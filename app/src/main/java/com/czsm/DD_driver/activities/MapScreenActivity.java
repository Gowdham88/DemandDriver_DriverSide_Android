package com.czsm.DD_driver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czsm.DD_driver.R;

public class MapScreenActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.user_service_details_toolbar);
        toolbar.setTitle("Map View");
    }
}
