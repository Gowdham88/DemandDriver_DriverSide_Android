package com.czsm.DD_driver.gcm;

import android.content.Context;

public class GCMBroadcastReceiver extends
        com.pushbots.google.gcm.GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {

        return "com.czsm.driverin.gcm.GCMIntentService";

    }
}