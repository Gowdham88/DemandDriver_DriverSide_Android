package com.aurorasdp.allinall.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.activities.UserOngoingBookingActivity;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.UserBooking;
import com.aurorasdp.allinall.receiver.NotificationBroadcastReceiver;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingAppointmentsFragment extends Fragment implements RESTClient.ServiceResponseInterface {


    private SwipeRefreshLayout swipeContainer;
    private AllinAllController allinAllController;
    private OngoingListAdapter adapter;
    private ListView listView;
    private NotificationBroadcastReceiver mReceiver;

    public OngoingAppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allinAllController = new AllinAllController(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing_appointments, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        listView = (ListView) view.findViewById(R.id.fragment_ongoing_appointments_listview);
        allinAllController.getOngoingAppointments(RESTClient.ID, "Loading Bookings ....");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserBooking userBooking = adapter.getItem(position);
                UserOngoingBookingActivity.bookingIndex = position;
                Intent bookingIntent = new Intent(getContext(), UserOngoingBookingActivity.class);
                startActivity(bookingIntent);
            }
        });


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allinAllController.getOngoingAppointments(RESTClient.ID, null);
            }
        });

        TextView emptyView = Util.getEmptyView(R.string.no_bookings, getContext());
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        mReceiver = new NotificationBroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(getContext(), "MEssage Received", Toast.LENGTH_LONG).show();
                Bundle extra = intent.getExtras();
                if (extra != null && extra.getString("Push") != null && extra.getString("Push").equalsIgnoreCase("1")) {
                    allinAllController.getOngoingAppointments(RESTClient.ID, null);
                }
            }
        };
        getActivity().registerReceiver(mReceiver, new IntentFilter(NotificationBroadcastReceiver.NOTIFICATION_RECEIVED));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (adapter == null) {
            adapter = new OngoingListAdapter(getContext(), R.layout.list_item_ongoing_bookings);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(getContext());
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private class OngoingListAdapter extends ArrayAdapter<UserBooking> {
        public View mView;
        public TextView userNameTextview;
        public TextView dateTimeTextview;
        public TextView serviceTextview;
        public ImageView providerImageview;
        public Context context;
        public int resource;


        public OngoingListAdapter(Context context, int resource) {
            super(context, resource, RESTClient.ONGOING_BOOKINGS);
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
            UserBooking booking = RESTClient.ONGOING_BOOKINGS.get(position);
            userNameTextview = (TextView) convertView.findViewById(R.id.list_item_booking_user_name_textview);
            dateTimeTextview = (TextView) convertView.findViewById(R.id.list_item_booking_datetime_textview);
            serviceTextview = (TextView) convertView.findViewById(R.id.list_item_booking_service_textview);
            providerImageview = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);

            userNameTextview.setText(booking.getProviderName());
            dateTimeTextview.setText(booking.getDateTime());
            serviceTextview.setText(booking.getService());
            if (booking.getDecodedPic() == null || booking.getDecodedPic().length == 0) {
                providerImageview.setImageResource(R.drawable.profile_circle);
            } else {
                byte[] decodedImage = booking.getDecodedPic();
                providerImageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
            }
            return convertView;
        }
    }

}

