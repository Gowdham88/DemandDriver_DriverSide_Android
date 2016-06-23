package com.aurorasdp.allinall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;
import com.pushbots.push.PBNotificationIntent;
import com.pushbots.push.Pushbots;
import com.pushbots.push.utils.PBConstants;

import java.util.HashMap;

public class PushNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "customHandler";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action=" + action);
        // Handle Push Message when opened
        if (action.equals(PBConstants.EVENT_MSG_OPEN)) {
            //Check for Pushbots Instance
            Pushbots pushInstance = Pushbots.sharedInstance();
            if (!pushInstance.isInitialized()) {
                Log.i("Initializing Pushbots.", "");
                Pushbots.sharedInstance().init(context.getApplicationContext());
            }

            //Clear Notification array
            if (PBNotificationIntent.notificationsArray != null) {
                PBNotificationIntent.notificationsArray = null;
            }

            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_OPEN);
            Log.i(TAG, "User clicked notification with Message: " + PushdataOpen.get("message"));

            //Report Opened Push Notification to Pushbots
            if (Pushbots.sharedInstance().isAnalyticsEnabled()) {
                Pushbots.sharedInstance().reportPushOpened((String) PushdataOpen.get("PUSHANALYTICS"));
            }

            if (PushdataOpen.get("mode") != null) {
                if (PushdataOpen.get("mode").toString().equals("CANCEL_APPOINTMENT") || PushdataOpen.get("mode").toString().equals("CONFIRM_APPOINTMENT")) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
                    RESTClient.ID = sharedPreferences.getString("userId", "");
                    if (!RESTClient.ID.equalsIgnoreCase("")) {
                        Intent resultIntent = new Intent(context, com.aurorasdp.allinall.activities.UserActivity.class);
                        resultIntent.putExtra("FROM_PUSH", "1");
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Pushbots.sharedInstance().startActivity(resultIntent);
                    } else {
                        Intent resultIntent = new Intent(context, com.aurorasdp.allinall.activities.LoginActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Pushbots.sharedInstance().startActivity(resultIntent);
                    }

                } else if (PushdataOpen.get("mode").toString().equals("PENDING_APPOINTMENT")) {
                    Intent resultIntent = new Intent(context, com.aurorasdp.allinall.activities.LoginActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Pushbots.sharedInstance().startActivity(resultIntent);
                } else if (PushdataOpen.get("mode").toString().equalsIgnoreCase("REJECT_APPOINTMENT")) {
//                    Toast.makeText(context, "Notification" + PushdataOpen.get("message"), Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent(context, com.aurorasdp.allinall.activities.UserActivity.class);
                    Bundle bundle = new Bundle();
//                    bundle.putString("user_message", PushdataOpen.get("message").toString());
                    bundle.putString("FROM_PUSH", "2");
                    resultIntent.putExtras(bundle);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Pushbots.sharedInstance().startActivity(resultIntent);
                }
//                Toast.makeText(ontext, "Notification" + RESTClient.ID, Toast.LENGTH_LONG).show();
            }

        } // Handle Push Message when received

        else if (action.equals(PBConstants.EVENT_MSG_RECEIVE))

        {
            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_RECEIVE);
            Log.i("MYAPP", "User Received notification with Message: " + PushdataOpen.get("message"));
            if (PushdataOpen.get("mode").toString().equals("PENDING_APPOINTMENT")) {
//                Toast.makeText(context, "Notification" + PushdataOpen.get("message").toString() + " -- " +PushdataOpen.get("appointment_id").toString(), Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(NotificationBroadcastReceiver.NOTIFICATION_RECEIVED);
                Bundle extra = new Bundle();
                extra.putString("message", PushdataOpen.get("message").toString());
                extra.putString("id", PushdataOpen.get("appointment_id").toString());
                intent1.putExtras(extra);
                context.sendBroadcast(intent1);
            } else if (PushdataOpen.get("mode").toString().equals("CONFIRM_APPOINTMENT") || PushdataOpen.get("mode").toString().equals("CANCEL_APPOINTMENT")) {
                Intent intent1 = new Intent(NotificationBroadcastReceiver.NOTIFICATION_RECEIVED);
                Bundle extra = new Bundle();
                extra.putString("Push", "1");
                intent1.putExtras(extra);
                context.sendBroadcast(intent1);
            } else if (PushdataOpen.get("mode").toString().equalsIgnoreCase("REJECT_APPOINTMENT")) {
//                Toast.makeText(context, "Notification ---- " + PushdataOpen.get("message"), Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
                String message = sharedPreferences.getString("user_message", "");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_message", message + "\n" + PushdataOpen.get("message").toString());
                editor.apply();
                Intent intent1 = new Intent(NotificationBroadcastReceiver.NOTIFICATION_RECEIVED);
                Bundle extra = new Bundle();
                extra.putString("user_message", PushdataOpen.get("message").toString());
                intent1.putExtras(extra);
                context.sendBroadcast(intent1);
            }
        }
    }
}

