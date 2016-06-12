package com.aurorasdp.allinall.gcm;

import android.content.Context;

public class GCMBroadcastReceiver extends
        com.pushbots.google.gcm.GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return "com.aurorasdp.allinall.gcm.GCMIntentService";
    }
}