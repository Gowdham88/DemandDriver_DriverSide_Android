package com.aurorasdp.allinall.fragments;


import android.content.Context;
import android.content.Intent;
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

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.activities.ProviderBookingActivity;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.ProviderBooking;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderBookingsFragment extends Fragment implements RESTClient.ServiceResponseInterface {

    private SwipeRefreshLayout swipeContainer;
    private AllinAllController allinAllController;
    private ProviderBookingsListAdapter adapter;
    private ListView listView;

    public ProviderBookingsFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_provider_bookings, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        listView = (ListView) view.findViewById(R.id.fragment_provider_book_listview);
        allinAllController.listProviderBookings(RESTClient.ID, "Loading Bookings ...");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProviderBooking providerBooking = adapter.getItem(position);
                Intent bookingIntent = new Intent(getContext(), ProviderBookingActivity.class);
                Bundle bookingBundle = new Bundle();
                bookingBundle.putInt("bookingIndex", position);
                bookingIntent.putExtras(bookingBundle);
                startActivity(bookingIntent);
            }
        });


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allinAllController.listProviderBookings(RESTClient.ID, null);
            }
        });

        TextView emptyView = Util.getEmptyView(R.string.no_bookings, getContext());
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        return view;
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (adapter == null) {
            adapter = new ProviderBookingsListAdapter(getContext(), R.layout.list_item_provider_booking);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(RESTClient.PROVIDER_BOOKINGS);
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(getContext());
    }

    private class ProviderBookingsListAdapter extends ArrayAdapter<ProviderBooking> {
        public View mView;
        public TextView userNameTextview;
        public TextView dateTimeTextview;
        public TextView serviceTextview;
        public ImageView userImageview;
        public Context context;
        public int resource;
        ArrayList<ProviderBooking> providerBookings;


        public ProviderBookingsListAdapter(Context context, int resource) {
            super(context, resource, RESTClient.PROVIDER_BOOKINGS);
            this.context = context;
            this.resource = resource;
            this.providerBookings = RESTClient.PROVIDER_BOOKINGS;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = viewInflater.inflate(resource, null);
            }
            mView = convertView;
            userNameTextview = (TextView) convertView.findViewById(R.id.list_item_booking_user_name_textview);
            dateTimeTextview = (TextView) convertView.findViewById(R.id.list_item_booking_datetime_textview);
            serviceTextview = (TextView) convertView.findViewById(R.id.list_item_booking_service_textview);
            userImageview = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);

            userNameTextview.setText(providerBookings.get(position).getUserName());
            dateTimeTextview.setText(providerBookings.get(position).getDateTime());
            serviceTextview.setText(providerBookings.get(position).getService());
            if (providerBookings.get(position).getDecodedPic() == null || providerBookings.get(position).getDecodedPic().length == 0) {
                userImageview.setImageResource(R.drawable.profile_circle);
            } else {
                byte[] decodedImage = providerBookings.get(position).getDecodedPic();
                userImageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
            }
            return convertView;
        }
    }
}
