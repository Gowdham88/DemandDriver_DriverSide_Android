package com.czsm.driverin.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czsm.driverin.Firebasemodel.AppointmentList;
import com.czsm.driverin.R;
import com.czsm.driverin.controller.AllinAllController;
import com.czsm.driverin.helper.RESTClient;
import com.czsm.driverin.helper.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by macbook on 02/08/16.
 */
public class UserHistoryActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {

    private SwipeRefreshLayout swipeContainer;
    private AllinAllController allinAllController;
    private UserHistoryListAdapter adapter;
    private ListView listView;

    DatabaseReference db;
    SharedPreferences preferences;
    ArrayList<AppointmentList> Bookinglist = new ArrayList<AppointmentList>();
    ArrayList<String> primaryidlist        = new ArrayList<String>();
    String id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_history);
        allinAllController = new AllinAllController(UserHistoryActivity.this, this);

        db                = FirebaseDatabase.getInstance().getReference();

        preferences       = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        id                = preferences.getString("userId","");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My History");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        swipeContainer  = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        listView        = (ListView) findViewById(R.id.fragment_user_history_listview);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent bookingIntent = new Intent(getApplicationContext(), UserBookingHistoryActivity.class);
                Bundle bookingBundle = new Bundle();
                bookingBundle.putString("drivername",    Bookinglist.get(position).getDrivername());
                bookingBundle.putString("id",            primaryidlist.get(position));
                bookingBundle.putString("driveraddress", Bookinglist.get(position).getDriveraddress());
                bookingBundle.putString("date",          Bookinglist.get(position).getDate()+" "+Bookinglist.get(position).getTime());
                bookingBundle.putString("review",        Bookinglist.get(position).getUserreview());
                bookingIntent.putExtras(bookingBundle);
                startActivity(bookingIntent);

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Appointment();

            }
        });


        TextView emptyView = Util.getEmptyView(R.string.no_history, getApplicationContext());
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

            Appointment();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void sendServiceResult(String serviceResult) {


    }

    public void Appointment(){

        ValueEventListener appointmentlistner = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    AppointmentList appointment  = child.getValue(AppointmentList.class);

                    if(appointment.getStatus().equals("Completed")) {

                        Bookinglist.clear();
                        primaryidlist.clear();
                        Bookinglist.add(appointment);
                        primaryidlist.add(child.getKey());

                        if (adapter == null) {

                            adapter = new UserHistoryListAdapter(getApplicationContext(), R.layout.list_item_user_history);
                            listView.setAdapter(adapter);

                        } else {

                            adapter.notifyDataSetChanged();
                            swipeContainer.setRefreshing(false);
                        }

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("loadPost:onCancelled", databaseError.toException().toString());
            }
        };

        db.child("AppointmentList").orderByChild("userid").equalTo(id).addValueEventListener(appointmentlistner);



    }




    @Override
    public void requestFailed() {
        Util.requestFailed(getApplicationContext());
    }

    private class UserHistoryListAdapter extends ArrayAdapter<AppointmentList> {
        public View mView;
        public TextView userNameTextview;
        public TextView dateTimeTextview;
        public TextView serviceTextview;
        public ImageView providerImageview;
        public Context context;
        public int resource;


        public UserHistoryListAdapter(Context context, int resource) {
            super(context, resource, Bookinglist);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = viewInflater.inflate(resource, null);
            }
            mView = convertView;
            AppointmentList booking = Bookinglist.get(position);
            userNameTextview  = (TextView) convertView.findViewById(R.id.list_item_booking_user_name_textview);
            dateTimeTextview  = (TextView) convertView.findViewById(R.id.list_item_booking_datetime_textview);
            serviceTextview   = (TextView) convertView.findViewById(R.id.list_item_booking_service_textview);
            providerImageview = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);

            userNameTextview.setText(booking.drivername);
            dateTimeTextview.setText(booking.getDate()+" "+booking.getTime());
            serviceTextview.setText(booking.getDriveraddress());

            if(booking.getDriverimage() != null)
                Picasso.with(context).load(booking.getDriverimage()).into(providerImageview);

            return convertView;
        }
    }

}
