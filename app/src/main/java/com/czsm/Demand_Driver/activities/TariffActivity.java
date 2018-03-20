package com.czsm.Demand_Driver.activities;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.adapters.TariffplanAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import android.support.annotation.NonNull;

public class TariffActivity extends AppCompatActivity {
    String serviceId;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.tariff_plan_assurance_textview)
    TextView assuranceTextview;
    @Nullable
    @BindView(R.id.tariff_plan_pricing_textview)
    TextView pricingTextview;
    RecyclerView recyclerView;
   TariffplanAdapter tariffadapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tariff);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            serviceId = extras.getString("serviceId");
            if (serviceId.equalsIgnoreCase("1")) { // call Driver
                setContentView(R.layout.activity_tariff);
                ButterKnife.bind(this);
                recyclerView=(RecyclerView) findViewById(R.id.tariff_plan_recyclerview);
                tariffadapter = new TariffplanAdapter(context);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(tariffadapter);
            } else {
                setContentView(R.layout.activity_tariff_plan);
                ButterKnife.bind(this);
                if (serviceId.equalsIgnoreCase("2")) { // car/pick mechanic
                    assuranceTextview.setText(getString(R.string.tariff_assurance_mechanic));
                    pricingTextview.setText(getString(R.string.tariff_pricing_mechanic));
                } else if (Arrays.asList(new String[]{"3", "4", "5"}).contains(serviceId)) {
                    assuranceTextview.setText(getString(R.string.tariff_assurance_elec_mas_pl_carp));
                    pricingTextview.setText(getString(R.string.tariff_pricing_elec_mas_pl_carp));
                }
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.tariff_plan));

    }
}
