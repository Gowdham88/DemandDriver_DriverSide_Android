package com.aurorasdp.allinall.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.Service;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserServicesFragment extends Fragment implements RESTClient.ServiceResponseInterface {

    private AllinAllController allinAllController;
    private ListView servicesListView;
    ServicesListAdapter adapter;

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
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        servicesListView = (ListView) view.findViewById(R.id.fragment_services_listview);
        return view;
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.service_get_services_success))) {
            adapter = new ServicesListAdapter(getContext(), R.layout.list_item_service);
            servicesListView.setAdapter(adapter);
        }
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
            serviceName = (TextView) convertView.findViewById(R.id.list_item_service_name_textview);
            serviceImage = (ImageView) convertView.findViewById(R.id.list_item_service_imageview);

            serviceName.setText(services.get(position).getServiceName());
            serviceImage.setImageResource(services.get(position).getImageResource());
            return convertView;
        }
    }
}
