package com.czsm.Demand_Driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.czsm.Demand_Driver.R;

/**
 * Created by czsm4 on 20/03/18.
 */

public class TariffplanAdapter extends RecyclerView.Adapter<TariffplanAdapter.ViewHolder>  {
    private final Context context;
    String[] arr = {"Food","Wine", "Rum", "Food", "Rum","Wine"};
    TextView textView, text11, text12, text13, text21, text22, text23;
    public TariffplanAdapter(Context context) {
        this.context=context;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tariffcontent, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textview1);
            text11 = (TextView) itemView.findViewById(R.id.text11);
            text12 = (TextView) itemView.findViewById(R.id.text12);
            text13 = (TextView) itemView.findViewById(R.id.text13);
            text21 = (TextView) itemView.findViewById(R.id.text21);
            text22 = (TextView) itemView.findViewById(R.id.text22);
            text23 = (TextView) itemView.findViewById(R.id.text23);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position == 0) {
            textView.setText("CITY PACKAGE");
            text11.setText("Min 12 Hours");
            text21.setText("RS. 650.00");
        } else if (position == 1) {
            textView.setText("HOURLY - SILVER");
            text11.setText("Min 4 Hours");
            text21.setText("RS. 280.00");
        } else if (position == 2) {
            textView.setText("HOURLY GOLD");
            text11.setText("Min 4 Hours");
            text21.setText("RS. 300.00");
        } else if (position == 3) {
            textView.setText("VALET PARKING");
            text11.setText("Min 5 Hours");
            text21.setText("RS. 500.00");
        }
        text12.setText("Extra per hour");
        text13.setText("Night Charges");
        text22.setText("RS. 50.00");
        text23.setText("RS. 0.00");

    }







    @Override
    public int getItemCount() {
        return arr.length;
    }
}
