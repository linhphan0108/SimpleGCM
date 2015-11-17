package com.linhphan.samplegcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by linhphan on 11/16/15.
 */
public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (!extras.isEmpty()){
            // read extras as sent from server
            String message = extras.getString("greeting");
            String from = extras.getString("from");
            String name = extras.getString("name");
            showNotification("greeting: " + message +
                    "\n" + "name: "+ name +
                    "\n" + "from: " + from);
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String msg) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification from GCM")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        builder.setContentIntent(pendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
