package com.czsm.driverin.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.czsm.driverin.Firebasemodel.AppointmentList;
import com.czsm.driverin.R;
import com.czsm.driverin.activities.ServiceProviderActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by macbook on 23/08/16.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    DatabaseReference db;
    String userid;

    @Override
    public void onReceive(final Context context, Intent intent) {

        // Vibrate the mobile phone

        db                = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        userid            = sharedPreferences.getString("providerId","");

        Toast.makeText(context,"dsss",Toast.LENGTH_SHORT).show();


        ValueEventListener appointmentlistner = new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    AppointmentList appointment  = child.getValue(AppointmentList.class);

                    if(appointment.getStatus().equals("pending")){

                        NotificationManager notificationManager = (NotificationManager)
                                context.getSystemService(Context.NOTIFICATION_SERVICE);

                        Intent intent = new Intent(context, ServiceProviderActivity.class);

                        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
                        Notification n  = new Notification.Builder(context)
                                .setContentTitle("New appointment has been booked")
                                .setContentText("Appointment has been booked on "+appointment.getTime()+appointment.getDate())
                                .setSmallIcon(R.drawable.login_logo)
                                .setContentIntent(pIntent)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(0, n);

                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(2000);

//                        showPendingBookingDialog("New appointment has been booked",child);

                    } else if(appointment.getStatus().equals("Completed") || appointment.getStatus().equals("Cancelled")){

                        Intent service = new Intent(context, CapPhoto.class);
                        context.stopService(service);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("loadPost:onCancelled", databaseError.toException().toString());
            }
        };

        db.child("AppointmentList").orderByChild("providerid").equalTo(userid).limitToLast(1).addValueEventListener(appointmentlistner);

    }


    }


