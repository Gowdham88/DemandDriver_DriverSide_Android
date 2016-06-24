package com.aurorasdp.allinall.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.activities.ProviderBookingActivity;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.ProviderBooking;
import com.aurorasdp.allinall.receiver.NotificationBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderBookingsFragment extends Fragment implements RESTClient.ServiceResponseInterface {

    private SwipeRefreshLayout swipeContainer;
    private AllinAllController allinAllController;
    private ProviderBookingsListAdapter adapter;
    private ListView listView;
    private NotificationBroadcastReceiver mReceiver;

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
        allinAllController.listProviderBookings(RESTClient.ID, "1", "Loading Bookings ...");
        allinAllController.getPendingAppointments(RESTClient.ID);
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
                allinAllController.listProviderBookings(RESTClient.ID, "0", null);
            }
        });

        TextView emptyView = Util.getEmptyView(R.string.no_bookings, getContext());
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        mReceiver = new NotificationBroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(getContext(), "MEssage Received", Toast.LENGTH_LONG).show();
                Bundle extra = intent.getExtras();
                if (extra != null && extra.getString("message") != null) {
                    showPendingBookingDialog(extra.getString("message"), extra.getString("id"));
                }
            }
        };
        getActivity().registerReceiver(mReceiver, new IntentFilter(NotificationBroadcastReceiver.NOTIFICATION_RECEIVED));
        return view;
    }

    private void showPendingBookingDialog(String message, final String id) {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("New Booking: ")
                .setMessage(message)
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        allinAllController.confirmAppointment(id);
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        allinAllController.rejectAppointment(id);
                    }
                }).show();
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.provider_list_bookings_success)) || serviceResult.equalsIgnoreCase(getString(R.string.provider_list_bookings_fail))) {
            if (adapter == null) {
                adapter = new ProviderBookingsListAdapter(getContext(), R.layout.list_item_provider_booking);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
//        } else if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_confirm_success)) || serviceResult.equalsIgnoreCase(getString(R.string.appointment_reject_success))) {
//            allinAllController.listProviderBookings(RESTClient.ID, "1", "Loading Bookings ...");
//            Toast.makeText(getContext(), serviceResult, Toast.LENGTH_LONG).show();

//        } else if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_reject_success))) {
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_pending_success))) {
            if (RESTClient.PENDING_MESSAGES != null && !RESTClient.PENDING_MESSAGES.equalsIgnoreCase("")) {
                String[] messagesList = TextUtils.split(RESTClient.PENDING_MESSAGES, ",");
                final String[] idsList = TextUtils.split(RESTClient.PENDING_IDS, ",");
                for (int i = 0; i < idsList.length; i++) {
                    showPendingBookingDialog(messagesList[i], idsList[i]);
                }
            }
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.appointment_confirm_success)))
            allinAllController.listProviderBookings(RESTClient.ID, "1", "Loading Bookings ...");
        else if (!serviceResult.equalsIgnoreCase(getString(R.string.appointment_reject_fail)) && !serviceResult.equalsIgnoreCase(getString(R.string.appointment_pending_fail)))
            Toast.makeText(getContext(), serviceResult, Toast.LENGTH_LONG).show();

    }

    @Override
    public void requestFailed() {
        Util.requestFailed(getContext());
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
            getActivity().unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private class ProviderBookingsListAdapter extends ArrayAdapter<ProviderBooking> {
        public View mView;
        public TextView userNameTextview;
        public TextView dateTimeTextview;
        public TextView serviceTextview;
        public ImageView userImageview;
        public Context context;
        public int resource;
//        ArrayList<ProviderBooking> providerBookings;


        public ProviderBookingsListAdapter(Context context, int resource) {
            super(context, resource, RESTClient.PROVIDER_BOOKINGS);
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
            ProviderBooking booking = RESTClient.PROVIDER_BOOKINGS.get(position);
            userNameTextview = (TextView) convertView.findViewById(R.id.list_item_booking_user_name_textview);
            dateTimeTextview = (TextView) convertView.findViewById(R.id.list_item_booking_datetime_textview);
            serviceTextview = (TextView) convertView.findViewById(R.id.list_item_booking_service_textview);
            userImageview = (ImageView) convertView.findViewById(R.id.list_item_booking_user_imagview);

            userNameTextview.setText(booking.getUserName());
            dateTimeTextview.setText(booking.getDateTime());
            serviceTextview.setText(booking.getService());
            if (booking.getDecodedPic() == null || booking.getDecodedPic().length == 0) {
                userImageview.setImageResource(R.drawable.profile_circle);
            } else {
                byte[] decodedImage = booking.getDecodedPic();
                userImageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
            }
            return convertView;
        }
    }
}
