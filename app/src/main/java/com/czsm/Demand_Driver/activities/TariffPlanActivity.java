package com.czsm.Demand_Driver.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.czsm.Demand_Driver.R;


import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class TariffPlanActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.tariff_plan_assurance_textview)
    TextView assuranceTextview;
    @Nullable
    @BindView(R.id.tariff_plan_pricing_textview)
    TextView pricingTextview;
    String serviceId;
//    @Optional
    @BindView(R.id.tariff_plan_listview)
    ListView tariffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            serviceId = extras.getString("serviceId");
            if (serviceId.equalsIgnoreCase("1")) { // call Driver
                setContentView(R.layout.activity_tariff_plan_car_driver);
                ButterKnife.bind(this);
                ArrayList<String> list = new ArrayList<String>();
                list.add(R.drawable.tariff_1 + "");
                list.add(R.drawable.tariff_1 + "");
                list.add(R.drawable.tariff_1 + "");
                list.add(R.drawable.tariff_1 + "");
                tariffList.setAdapter(new TarrifAdapter(this, R.layout.list_item_tariff_plan, list));
//                Util.setListViewHeightBasedOnChildren(tariffList);
            } else {
                setContentView(R.layout.activity_tariff_plan);
                ButterKnife.bind(this);
                if (serviceId.equalsIgnoreCase("2")) { // car/pick mechanic
//                    assuranceTextview.setText(getString(R.string.tariff_assurance_mechanic));
                    pricingTextview.setText(getString(R.string.tariff_pricing_mechanic));
                } else if (Arrays.asList(new String[]{"3", "4", "5"}).contains(serviceId)) {
//                    assuranceTextview.setText(getString(R.string.tariff_assurance_elec_mas_pl_carp));
                    pricingTextview.setText(getString(R.string.tariff_pricing_elec_mas_pl_carp));
                }
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.tariff_plan));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public static class TarrifAdapter extends ArrayAdapter<String> {
        Context context;
        int resource;
        ArrayList<String> list;
        TextView textView, text11, text12, text13, text21, text22, text23;

        public TarrifAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
            this.context = context;
            this.resource = resource;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = viewInflater.inflate(resource, parent, false);
            }
//            ImageView tariffImage = (ImageView) convertView.findViewById(R.id.list_item_tariff_imageview);
//            tariffImage.setImageResource(Integer.parseInt(list.get(position)));
            textView = (TextView) convertView.findViewById(R.id.textview1);
            text11 = (TextView) convertView.findViewById(R.id.text11);
            text12 = (TextView) convertView.findViewById(R.id.text12);
            text13 = (TextView) convertView.findViewById(R.id.text13);
            text21 = (TextView) convertView.findViewById(R.id.text21);
            text22 = (TextView) convertView.findViewById(R.id.text22);
            text23 = (TextView) convertView.findViewById(R.id.text23);
            if (position == 0) {
                textView.setText("CITY PACKAGE");
                text11.setText("Min 12 Hours");
                text21.setText("RS. 650.00");
            } else if (position == 1) {
                textView.setText("HOURLY - SILVER");
                text11.setText("Min 4 Hours");
                text21.setText("RS. 280.00");
            } else if (position == 2) {
                textView.setText("HOURLY GOLD");
                text11.setText("Min 4 Hours");
                text21.setText("RS. 300.00");
            } else if (position == 3) {
                textView.setText("VALET PARKING");
                text11.setText("Min 5 Hours");
                text21.setText("RS. 500.00");
            }
            text12.setText("Extra per hour");
            text13.setText("Night Charges");
            text22.setText("RS. 50.00");
            text23.setText("RS. 0.00");
            return convertView;
        }
    }
}
