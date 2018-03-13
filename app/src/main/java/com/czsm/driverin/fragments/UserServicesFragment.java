package com.czsm.driverin.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.driverin.R;
import com.czsm.driverin.activities.BookServiceMapActivity;
import com.czsm.driverin.activities.UserActivity;
import com.czsm.driverin.controller.AllinAllController;
import com.czsm.driverin.helper.RESTClient;
import com.czsm.driverin.helper.Util;
import com.czsm.driverin.model.Service;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserServicesFragment extends Fragment implements RESTClient.ServiceResponseInterface {

    private AllinAllController allinAllController;
    private ListView servicesListView;
    ServicesListAdapter adapter;
    private Service service;

    public UserServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allinAllController = new AllinAllController(getContext(), this);
        allinAllController.getServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view        = inflater.inflate(R.layout.fragment_services, container, false);
        servicesListView = (ListView) view.findViewById(R.id.fragment_services_listview);
        servicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                service = (Service) servicesListView.getAdapter().getItem(position);
                if (service != null)
                    allinAllController.getServiceProviderList(service.getServiceId());
            }
        });
        return view;
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.service_get_services_success))) {
            adapter = new ServicesListAdapter(getContext(), R.layout.list_item_service);
            servicesListView.setAdapter(adapter);
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.service_get_providers_success))) {
            Intent serviceDetailsIntent = new Intent(getContext(), BookServiceMapActivity.class);
            Bundle serviceBundle = new Bundle();
            serviceBundle.putString("serviceId", service.getServiceId());
            serviceBundle.putString("serviceName", service.getServiceLabel());
            serviceBundle.putInt("serviceImageId", service.getImageResource());
            serviceDetailsIntent.putExtras(serviceBundle);
            startActivity(serviceDetailsIntent);
        }
       /* else if (serviceResult.equalsIgnoreCase(getString(R.string.service_get_providers_fail))) {
            Toast.makeText(getContext(), serviceResult, Toast.LENGTH_LONG).show();
            Intent serviceDetailsIntent = new Intent(getContext(), BookServiceMapActivity.class);
            Bundle serviceBundle = new Bundle();
            serviceBundle.putString("serviceId", service.getServiceId());
            serviceBundle.putString("serviceName", service.getServiceLabel());
            serviceBundle.putInt("serviceImageId", service.getImageResource());
            serviceDetailsIntent.putExtras(serviceBundle);
            startActivity(serviceDetailsIntent);
        }*/
        else
            Toast.makeText(getContext(), serviceResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(getContext());
    }

    private class ServicesListAdapter extends ArrayAdapter<Service> {
        public View mView;
        public TextView serviceName;
        public ImageView serviceImage;
        ArrayList<Service> services;
        int resource;
        Context context;

        public ServicesListAdapter(Context context, int resource) {
            super(context, resource, RESTClient.SERVICES);
            this.resource = resource;
            this.context = context;
            services = RESTClient.SERVICES;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = viewInflater.inflate(resource, parent, false);
            }
            serviceName  = (TextView) convertView.findViewById(R.id.list_item_service_name_textview);
            serviceImage = (ImageView) convertView.findViewById(R.id.list_item_service_imageview);
            int proportionalHeight = containerHeight(context);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight); // (width, height)
            convertView.setLayoutParams(params);
            serviceName.setText(services.get(position).getServiceName());
            serviceImage.setImageResource(services.get(position).getImageResource());
            return convertView;
        }

        public int containerHeight(Context fragment) {
            DisplayMetrics dm = new DisplayMetrics();
            ((UserActivity) fragment).getWindowManager().getDefaultDisplay().getMetrics(dm);
            double ratio = 5;
            return (int) (dm.heightPixels / ratio);
        }
    }
}
