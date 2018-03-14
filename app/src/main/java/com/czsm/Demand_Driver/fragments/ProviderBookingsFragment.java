package com.czsm.Demand_Driver.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.Demand_Driver.Firebasemodel.AppointmentList;
import com.czsm.Demand_Driver.Firebasemodel.ServiceproviderList;
import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.Service.CapPhoto;
import com.czsm.Demand_Driver.activities.ProviderBookingActivity;
import com.czsm.Demand_Driver.helper.Util;
import com.czsm.Demand_Driver.receiver.NotificationBroadcastReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderBookingsFragment extends Fragment  {

    private SwipeRefreshLayout swipeContainer;

    private ProviderBookingsListAdapter adapter;
    private ListView listView;
    private NotificationBroadcastReceiver mReceiver;
    DatabaseReference db;
    SharedPreferences sharedPreferences;
    String userid;
    AppointmentList appointment;
    ArrayList<AppointmentList> Bookinglist = new ArrayList<AppointmentList>();
    ArrayList<String> primaryidlist        = new ArrayList<String>();

    public ProviderBookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view      = inflater.inflate(R.layout.fragment_provider_bookings, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        listView       = (ListView) view.findViewById(R.id.fragment_provider_book_listview);

        db                = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        userid            = sharedPreferences.getString("providerId","");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent bookingIntent = new Intent(getContext(), ProviderBookingActivity.class);
                Bundle bookingBundle = new Bundle();
                bookingBundle.putString("username", Bookinglist.get(position).getUsername());
                bookingBundle.putString("usermobile", Bookinglist.get(position).getUsermobilenumber());
                bookingBundle.putString("useraddress", Bookinglist.get(position).getUseraddress());
                bookingBundle.putString("date",     Bookinglist.get(position).getDate()+" "+Bookinglist.get(position).getTime());
                bookingBundle.putString("userid", primaryidlist.get(position));
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

        TextView emptyView = Util.getEmptyView(R.string.no_bookings, getContext());
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        Appointment();

        return view;
    }

    private void showPendingBookingDialog(String message,final DataSnapshot child) {

        new android.support.v7.app.AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("New Booking: ")
                .setMessage(message)
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    if(!appointment.getStatus().equals("Confirmed")) {

                        Map appointmentData = new HashMap();
                        appointmentData.put("status", "Confirmed");
                        appointmentData.put("providerid", userid);
                        child.getRef().updateChildren(appointmentData);

                        Intent service = new Intent(getActivity(), CapPhoto.class);
                        service.putExtra("id",child.getKey());
                        service.putExtra("name",appointment.getDrivername());
                        service.putExtra("userid",appointment.getUserid());
                        getActivity().startService(service);

                        /*****************Updating driver********************************/

                        ValueEventListener maplistner = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                    ServiceproviderList list = child.getValue(ServiceproviderList.class);

                                    child.getRef().child("status").setValue("onduty");
                                    child.getRef().child("bookings_count").setValue(list.getBookings_count()+1);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.e("loadPost:onCancelled", databaseError.toException().toString());
                            }
                        };

                        db.child("ServiceproviderList").orderByKey().equalTo(userid).addListenerForSingleValueEvent(maplistner);

                    } else {

                        Toast.makeText(getActivity(),"Booking has already taken",Toast.LENGTH_SHORT).show();

                    }

                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        try {

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    public void Appointment(){

        ValueEventListener appointmentlistner = new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    appointment  = child.getValue(AppointmentList.class);

                    Bookinglist.clear();
                    primaryidlist.clear();

                    if(appointment.getStatus().equals("pending")){

                        showPendingBookingDialog("New appointment has been booked",child);

                    } else if(appointment.getStatus().equals("Confirmed")) {


                        Bookinglist.add(appointment);
                        primaryidlist.add(child.getKey());


                    }

                    if (adapter == null) {

                        adapter = new ProviderBookingsListAdapter(getContext(), R.layout.list_item_provider_booking);
                        listView.setAdapter(adapter);

                    } else {

                        adapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("loadPost:onCancelled", databaseError.toException().toString());
            }
        };

        db.child("AppointmentList").orderByChild("providerid").equalTo(userid).limitToLast(1).addValueEventListener(appointmentlistner);

    }






    private class ProviderBookingsListAdapter extends ArrayAdapter<AppointmentList> {

        public View mView;
        public TextView userNameTextview;
        public TextView dateTimeTextview;
        public TextView serviceTextview;
        public ImageView userImageview;
        public Context context;
        public int resource;
//        ArrayList<ProviderBooking> providerBookings;


        public ProviderBookingsListAdapter(Context context, int resource) {
            super(context, resource, Bookinglist);
            this.context = context;
            this.resource = resource;
//            this.providerBookings = RESTClient.PROVIDER_BOOKINGS;
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
            userImageview           = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);

            userNameTextview.setText(booking.getUsername());
            dateTimeTextview.setText(booking.getDate()+"-"+booking.getTime());
            serviceTextview.setText(booking.getUseraddress());

            if(booking.getUserimage() != null)
                Picasso.with(context).load(booking.getUserimage()).into(userImageview);

            return convertView;
        }
    }
}
