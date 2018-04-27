package com.czsm.DD_driver.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.activities.ProviderBookingActivity;
import com.czsm.DD_driver.model.Data;
import com.czsm.DD_driver.model.Driver_current_Details;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czsm4 on 10/04/18.
 */

public class ProviderBookingAdapter extends RecyclerView.Adapter<ProviderBookingAdapter.ViewHolder> {

    Context context;
    List<Driver_current_Details> dataList = new ArrayList<>();


    public ProviderBookingAdapter(Context context, List<Driver_current_Details> dataList) {
        this.context  = context;
        this.dataList = dataList;
//        this.cellSize = Utils.getScreenWidth(context)/3;
    }
    public  void addData(List<Driver_current_Details> stringArrayList){
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

        holder.userphonenumber.setText(dataList.get(position).getUser_Phone_number());
        final String DateTime=dataList.get(position).getDate()+" "+dataList.get(position).getUser_Booking_Time();
        holder.datetime.setText(DateTime);
        String useuuid=dataList.get(position).getUser_ID();
        holder.lat.setText(dataList.get(position).getUser_name());
        String address=dataList.get(position).getUser_Address();
        final String Drivername= PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_DRIVERNAME);
        holder.ProviderLinLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, ProviderBookingActivity.class);
                intent.putExtra("phonenumber",dataList.get(position).getUser_Phone_number());
                intent.putExtra("name",dataList.get(position).getUser_name());
                intent.putExtra("datatime",dataList.get(position).getUser_Book_Date_Time());
                intent.putExtra("address",dataList.get(position).getUser_Address());
                intent.putExtra("userlats",dataList.get(position).getStart_Lat());
                intent.putExtra("userlongs",dataList.get(position).getStart_Long());
                intent.putExtra("userdate",dataList.get(position).getDate());
                intent.putExtra("usertime",dataList.get(position).getUser_Booking_Time());
                intent.putExtra("useruid",dataList.get(position).getUser_ID());
                intent.putExtra("Car_type",dataList.get(position).getCar_type());
                intent.putExtra("Booking_ID",dataList.get(position).getUser_Booking_ID());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            }
        });


    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userphonenumber,datetime,lat;
        LinearLayout ProviderLinLay;
        public ViewHolder(View itemView) {
            super(itemView);
            userphonenumber=(TextView)itemView.findViewById(R.id.list_item_booking_service_textview);
            datetime=(TextView)itemView.findViewById(R.id.list_item_booking_datetime_textview);
            lat=(TextView)itemView.findViewById(R.id.list_item_booking_user_name_textview);
                    ProviderLinLay=(LinearLayout)itemView.findViewById(R.id.providerlinear_lay);

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
