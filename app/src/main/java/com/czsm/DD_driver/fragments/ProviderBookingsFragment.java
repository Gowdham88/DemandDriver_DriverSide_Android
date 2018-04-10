package com.czsm.DD_driver.fragments;


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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.Firebasemodel.AppointmentList;
import com.czsm.DD_driver.Firebasemodel.ServiceproviderList;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.Service.CapPhoto;
import com.czsm.DD_driver.activities.MapScreenActivity;
import com.czsm.DD_driver.activities.ProviderBookingActivity;
import com.czsm.DD_driver.adapters.ProviderBookingAdapter;
import com.czsm.DD_driver.helper.Util;
import com.czsm.DD_driver.model.Data;
import com.czsm.DD_driver.receiver.NotificationBroadcastReceiver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderBookingsFragment extends Fragment  {

    private SwipeRefreshLayout swipeContainer;

//    private ProviderBookingsListAdapter adapter;
ProviderBookingAdapter  providerAdapter ;
    RecyclerView recyclerview;
    private NotificationBroadcastReceiver mReceiver;
//    DatabaseReference db;
    SharedPreferences sharedPreferences;
    String userid;
    AppointmentList appointment;
    ArrayList<AppointmentList> Bookinglist = new ArrayList<AppointmentList>();
    ArrayList<String> primaryidlist        = new ArrayList<String>();
    FirebaseFirestore db;
    List<Data> datalist = new ArrayList<Data>();
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
        recyclerview       = (RecyclerView) view.findViewById(R.id.fragment_provider_book_listview);

        db = FirebaseFirestore.getInstance();

//        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
//        userid            = sharedPreferences.getString("providerId","");


        com.google.firebase.firestore.Query driverfirst = db.collection("UsersCurrentBooking");

        driverfirst.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocuments().size() < 1) {

                            return;

                        }

                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                            Data data = document.toObject(Data.class);
                            datalist.add(data);
//                            Log.e("lattitude", String.valueOf(Userlat));
//                            Log.e("longtude", String.valueOf(UserLong));
//                            Toast.makeText(CurrentReqActivity.this, (CharSequence) AddressTxt, Toast.LENGTH_SHORT).show();

//                                                  Log.e("datalist",datalist.get(0).getLat());
//                                hideProgressDialog();

                        }
//                            hideProgressDialog();


                    }
                });
        providerAdapter =new ProviderBookingAdapter(getActivity(),datalist);
        recyclerview.setAdapter(providerAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//                Intent bookingIntent = new Intent(getContext(), ProviderBookingActivity.class);
//                Bundle bookingBundle = new Bundle();
//                bookingBundle.putString("username", datalist.get(position).getCurrentlat());
//                bookingBundle.putString("usermobile", datalist.get(position).getPhoneNumber());
//                bookingBundle.putString("useraddress", datalist.get(position).getAddress());
//                bookingBundle.putString("date",     datalist.get(position).getDate()+" "+Bookinglist.get(position).getTime());
////                bookingBundle.putString("userid", primaryidlist.get(position));
//                bookingIntent.putExtras(bookingBundle);
//                startActivity(bookingIntent);
//            }
//        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Appointment();

            }
        });


//        TextView emptyView = Util.getEmptyView(R.string.no_bookings, getContext());
//        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
//        listView.setEmptyView(emptyView);

//        Appointment();

        return view;
    }

    private void Appointment() {
        providerAdapter =new ProviderBookingAdapter(getActivity(),datalist);
        recyclerview.setAdapter(providerAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        swipeContainer.setRefreshing(false);
    }

//    private void showPendingBookingDialog(String message,final DataSnapshot child) {
//
//        new android.support.v7.app.AlertDialog.Builder(getActivity())
//                //set message, title, and icon
//                .setTitle("New Booking: ")
//                .setMessage(message)
//                .setIcon(R.drawable.ic_launcher)
//                .setCancelable(false)
//                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                    if(!appointment.getStatus().equals("Confirmed")) {
//
//                        Map appointmentData = new HashMap();
//                        appointmentData.put("status", "Confirmed");
//                        appointmentData.put("providerid", userid);
//                        child.getRef().updateChildren(appointmentData);
//
//                        Intent service = new Intent(getActivity(), CapPhoto.class);
//                        service.putExtra("id",child.getKey());
//                        service.putExtra("name",appointment.getDrivername());
//                        service.putExtra("userid",appointment.getUserid());
//                        getActivity().startService(service);
//
//                        /*****************Updating driver********************************/
//
//                        ValueEventListener maplistner = new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                                    ServiceproviderList list = child.getValue(ServiceproviderList.class);
//
//                                    child.getRef().child("status").setValue("onduty");
//                                    child.getRef().child("bookings_count").setValue(list.getBookings_count()+1);
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                                Log.e("loadPost:onCancelled", databaseError.toException().toString());
//                            }
//                        };
//
//                        db.child("ServiceproviderList").orderByKey().equalTo(userid).addListenerForSingleValueEvent(maplistner);
//
//                    } else {
//
//                        Toast.makeText(getActivity(),"Booking has already taken",Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    }
//                })
//                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).show();
//    }



    @Override
    public void onResume() {
        super.onResume();
//        if (providerAdapter != null)
//            ProviderBookingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        try {

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


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
//
//                    Bookinglist.clear();
//                    primaryidlist.clear();
//
//                    if(appointment.getStatus().equals("pending")){
//
//                        showPendingBookingDialog("New appointment has been booked",child);
//
//                    } else if(appointment.getStatus().equals("Confirmed")) {
//
//
//                        Bookinglist.add(appointment);
//                        primaryidlist.add(child.getKey());
//
//
//                    }
//
//                    if (adapter == null) {
//
//                        adapter = new ProviderBookingsListAdapter(getContext(), R.layout.list_item_provider_booking);
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
//        db.child("AppointmentList").orderByChild("providerid").equalTo(userid).limitToLast(1).addValueEventListener(appointmentlistner);
//
//    }






//    private class ProviderBookingsListAdapter extends ArrayAdapter<Data> {
//
//        public View mView;
//        public TextView userNameTextview;
//        public TextView dateTimeTextview;
//        public TextView serviceTextview;
//        public ImageView userImageview;
//        public Context context;
//        public int resource;
////        ArrayList<ProviderBooking> providerBookings;
//
//
//        public ProviderBookingsListAdapter(Context context, int resource) {
//            super(context, resource, datalist);
//            this.context = context;
//            this.resource = resource;
////            this.providerBookings = RESTClient.PROVIDER_BOOKINGS;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(
//                        Context.LAYOUT_INFLATER_SERVICE);
//                convertView = viewInflater.inflate(resource, null);
//            }
//
//            mView = convertView;
//            Data booking = datalist.get(position);
//            userNameTextview        = (TextView) convertView.findViewById(R.id.list_item_booking_user_name_textview);
//            dateTimeTextview        = (TextView) convertView.findViewById(R.id.list_item_booking_datetime_textview);
//            serviceTextview         = (TextView) convertView.findViewById(R.id.list_item_booking_service_textview);
//            userImageview           = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);
//
//            userNameTextview.setText(booking.getCurrentlat());
//            dateTimeTextview.setText(booking.getDate()+"-"+booking.getTime());
//            serviceTextview.setText(booking.getAddress());
//
////            if(booking.getUserimage() != null)
////                Picasso.with(context).load(booking.getUserimage()).into(userImageview);
//
//            return convertView;
//        }
//    }
}
