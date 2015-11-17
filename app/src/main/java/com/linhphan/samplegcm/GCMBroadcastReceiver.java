package com.linhphan.samplegcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by linhphan on 11/16/15.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        // Attach component of GCMIntentService that will handle the intent in background thread
        ComponentName componentName = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, intent.setComponent(componentName));
        setResultCode(Activity.RESULT_OK);
    }
}
