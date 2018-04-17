package com.czsm.DD_driver.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.czsm.DD_driver.model.Driver_current_Details;
import com.czsm.DD_driver.receiver.NotificationBroadcastReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

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
    public FirebaseFirestore db;
    List<Driver_current_Details> datalist = new ArrayList<Driver_current_Details>();
    int i;
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


        dataload() ;

//hideProgressDialog();

        providerAdapter =new ProviderBookingAdapter(getActivity(),datalist);
        recyclerview.setAdapter(providerAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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

    private void dataload() {
        datalist.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("Current_booking").orderBy("User_Book_Date_Time", Query.Direction.DESCENDING);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocuments().size() < 1) {

                            return;

                        }

                        for(DocumentSnapshot document : documentSnapshots.getDocuments()) {

                            Driver_current_Details data = document.toObject(Driver_current_Details.class);
                            datalist.add(data);


                            String USEruid=  datalist.get(i).getUser_Booking_ID();

//                            Toast.makeText(getContext(), USEruid, Toast.LENGTH_SHORT).show();



                        }
                        providerAdapter.notifyDataSetChanged();
                    }



                });
    }

    private void Appointment() {
        dataload();

        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

//    }
}
