package com.czsm.DD_driver.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsm.DD_driver.R;
import com.czsm.DD_driver.activities.ProviderBookingActivity;
import com.czsm.DD_driver.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czsm4 on 10/04/18.
 */

public class ProviderBookingAdapter extends RecyclerView.Adapter<ProviderBookingAdapter.ViewHolder> {

    Context context;
    List<Data> dataList = new ArrayList<>();


    public ProviderBookingAdapter(Context context, List<Data> dataList) {
        this.context  = context;
        this.dataList = dataList;
//        this.cellSize = Utils.getScreenWidth(context)/3;
    }
    public  void addData(List<Data> stringArrayList){
        dataList.addAll(stringArrayList);
    }
    @Override
    public ProviderBookingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.
                        list_item_provider_booking, parent, false);

        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ProviderBookingAdapter.ViewHolder holder, final int position) {

        holder.userphonenumber.setText(dataList.get(position).getPhoneNumber());
        final String DateTime=dataList.get(position).getDate()+","+dataList.get(position).getTime();
        holder.datetime.setText(DateTime);
        holder.lat.setText(dataList.get(position).getCurrentlat());
        String address=dataList.get(position).getAddress();

        holder.ProviderLinLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, ProviderBookingActivity.class);
                intent.putExtra("phonenumber",dataList.get(position).getPhoneNumber());
                intent.putExtra("lat",dataList.get(position).getCurrentlat());
                intent.putExtra("datatime",DateTime);
                intent.putExtra("address",dataList.get(position).getAddress());
                intent.putExtra("userlats",dataList.get(position).getCurrentlat());
                intent.putExtra("userlongs",dataList.get(position).getCurrentlong());

                context.startActivity(intent);
            }
        });


    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userphonenumber,datetime,lat;
        LinearLayout ProviderLinLay;
        public ViewHolder(View itemView) {
            super(itemView);
            userphonenumber=(TextView)itemView.findViewById(R.id.list_item_booking_user_name_textview);
            datetime=(TextView)itemView.findViewById(R.id.list_item_booking_service_textview);
            lat=(TextView)itemView.findViewById(R.id.list_item_booking_datetime_textview);
                    ProviderLinLay=(LinearLayout)itemView.findViewById(R.id.providerlinear_lay);

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
