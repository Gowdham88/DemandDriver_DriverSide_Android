package com.czsm.Demand_Driver.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.activities.LoginActivity;


public class GCMIntentService extends com.pushbots.push.GCMIntentService {

private boolean alreadyRegistered = false;

@Override
protected void onRegistered(Context context, String registrationId) {
    super.onRegistered(context, registrationId);
    Log.i("GCMIntentService", "Device registered: regId = " + registrationId);

    Log.e("GCMIntentService", "Device registered: regId = " + registrationId);
//        Toast.makeText(getApplicationContext(), "Device Registered", Toast.LENGTH_LONG).show();
//        Pushbots.sharedInstance().setAlias(App.deviceID);
    if (!alreadyRegistered) {
        SharedPreferences allinAllSharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = allinAllSharedPreferences.edit();
        editor.putString("regID", registrationId);
        editor.apply();
        alreadyRegistered = true;
    }
    if (LoginActivity.loading != null && LoginActivity.loading.isShowing())
        LoginActivity.loading.dismiss();
}


}