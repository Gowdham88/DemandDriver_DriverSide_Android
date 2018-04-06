package com.czsm.DD_driver.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import com.czsm.DD_driver.Firebasemodel.AppointmentList;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.helper.RESTClient;
import com.czsm.DD_driver.helper.Util;
import com.czsm.DD_driver.receiver.NotificationBroadcastReceiver;
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
public class OngoingAppointmentActivity extends AppCompatActivity implements RESTClient.ServiceResponseInterface {


    private SwipeRefreshLayout swipeContainer;

    private OngoingListAdapter adapter;
    private ListView listView;
    private NotificationBroadcastReceiver mReceiver;

    AppointmentList appointment;
    ArrayList<AppointmentList> Bookinglist = new ArrayList<AppointmentList>();
    ArrayList<String> primaryidlist        = new ArrayList<String>();


    DatabaseReference db;
    SharedPreferences sharedPreferences;
    String userid;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ongoing_appointments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Current Appointment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        listView       = (ListView) findViewById(R.id.fragment_ongoing_appointments_listview);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        userid            = sharedPreferences.getString("userId","");
        db                 = FirebaseDatabase.getInstance().getReference();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent bookingIntent = new Intent(getApplicationContext(), UserOngoingBookingActivity.class);
                Bundle bookingBundle = new Bundle();
                bookingBundle.putString("drivername", Bookinglist.get(position).getDrivername());
                bookingBundle.putString("status", Bookinglist.get(position).getStatus());
                bookingBundle.putString("driveraddress", Bookinglist.get(position).getDriveraddress());
                bookingBundle.putString("date",     Bookinglist.get(position).getDate()+" "+Bookinglist.get(position).getTime());
                bookingBundle.putString("userid", primaryidlist.get(position));
                bookingBundle.putString("providerid", Bookinglist.get(position).getProviderid());
                bookingIntent.putExtras(bookingBundle);
                startActivity(bookingIntent);


            }
        });


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//                Appointment();

            }
        });

        TextView emptyView = Util.getEmptyView(R.string.no_bookings, getApplicationContext());
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

//        Appointment();

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

    @Override
    public void requestFailed() {

        Util.requestFailed(getApplicationContext());
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

//
//    public void Appointment(){
//
//        ValueEventListener appointmentlistner = new ValueEventListener() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                    appointment  = child.getValue(AppointmentList.class);
//                    Bookinglist.clear();
//                    primaryidlist.clear();
//
//                    if(appointment.getStatus().equals("pending") || appointment.getStatus().equals("Confirmed")) {
//
//                        Bookinglist.add(appointment);
//                        primaryidlist.add(child.getKey());
//
//                    }
//
//                    if (adapter == null) {
//
//                        adapter = new OngoingListAdapter(getApplicationContext(), R.layout.list_item_ongoing_bookings);
//                        listView.setAdapter(adapter);
//
//                    } else {
//
//                        adapter.notifyDataSetChanged();
//                        swipeContainer.setRefreshing(false);
//
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                Log.e("loadPost:onCancelled", databaseError.toException().toString());
//            }
//        };
//
//        db.child("AppointmentList").orderByChild("userid").equalTo(userid).addValueEventListener(appointmentlistner);
//
//
//
//    }





    private class OngoingListAdapter extends ArrayAdapter<AppointmentList> {
        public View mView;
        public TextView userNameTextview;
        public TextView dateTimeTextview;
        public TextView serviceTextview;
        public ImageView providerImageview;
        public Context context;
        public int resource;


        public OngoingListAdapter(Context context, int resource) {
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
            userNameTextview        = (TextView) convertView.findViewById(R.id.list_item_booking_user_name_textview);
            dateTimeTextview        = (TextView) convertView.findViewById(R.id.list_item_booking_datetime_textview);
            serviceTextview         = (TextView) convertView.findViewById(R.id.list_item_booking_service_textview);
            providerImageview       = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);

            userNameTextview.setText(booking.getDrivername());
            dateTimeTextview.setText(booking.getDate()+""+booking.getTime());
            serviceTextview.setText(booking.getProviderid());

            if(booking.getDriverimage() != null)
                Picasso.with(context).load(booking.getDriverimage()).into(providerImageview);

            return convertView;
        }
    }

}


