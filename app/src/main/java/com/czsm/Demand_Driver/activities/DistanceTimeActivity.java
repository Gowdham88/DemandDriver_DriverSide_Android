//package com.czsm.driverin.activities;
//
//import android.app.ProgressDialog;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.TextView;
//
//import com.czsm.driverin.R;
//import com.czsm.driverin.model.TimeModelClass;
//
//import retrofit.Callback;
//import retrofit.RestAdapter;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//
///**
// * Created by macbook on 27/07/16.
// */
//public class DistanceTimeActivity extends AppCompatActivity {
//
//    TextView Txt_distance;
//    public static String BaseUrl;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.distance);
//
//        Txt_distance = (TextView) findViewById(R.id.txt_time);
//
//        Distance();
//    }
//
//
//    public void Distance() {
//
//        final ProgressDialog dialog = ProgressDialog.show(this,"Fetching data","Please wait...",false,false);
//
//        StringBuilder googlePlacesUrl = new StringBuilder("/api/distancematrix/json?");
//        googlePlacesUrl.append("origins=" + "13.0692" + "," + "80.1914");
//        googlePlacesUrl.append("&destinations=" + "12.9010" + "," + "80.2279");
//        googlePlacesUrl.append("&key=" + "AIzaSyDv2rBW15Rnox8k13gIrgr5ksSerLqf2T0");
//
//        BaseUrl = googlePlacesUrl.toString();
//
//        RestAdapter adapter = new RestAdapter.Builder()
//                .setEndpoint("https://maps.googleapis.com/maps")
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .build();
//
//
//        DistanceApi distanceApi =  adapter.create(DistanceApi.class);
//
//        distanceApi.getDistance(new Callback<TimeModelClass>() {
//            @Override
//            public void success(TimeModelClass timeModelClass, Response response) {
//
//
//                if(timeModelClass.getStatus().equals("OK")){
//
//                    dialog.dismiss();
//
//                    Txt_distance.setText(timeModelClass.getRows().get(0).getElements().get(0).getDistance().getText());
//
//
//                } else {
//
//                    dialog.dismiss();
//
//                }
//
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//                dialog.dismiss();
//
//            }
//        });
//
//
//    }
//
//
//
//
//
//}
