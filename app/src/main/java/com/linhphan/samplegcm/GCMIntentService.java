package com.linhphan.samplegcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/**
 * Created by linhphan on 11/16/15.
 */
public class GCMIntentService extends IntentService implements MediaPlayer.OnCompletionListener {

    public static final String ARG_MESSAGE = "ARG_MESSAGE";

    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    public static MediaPlayer mPlayer;

    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())){
            String message = "GCM service has been registered";
            showNotification(message);

        }else{//== com.google.android.c2dm.intent.RECEIVE
            if (!extras.isEmpty()){
                // read extras as sent from server
                String message = extras.getString("message");
                showNotification(message);
                playSound();
                makeVibrate();
            }
        }


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String msg) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ARG_MESSAGE, msg);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Urgent")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        builder.setContentIntent(pendingIntent);

        Notification notify = builder.build();
        notify.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(NOTIFICATION_ID, notify);
    }

    private void playSound(){
        if (mPlayer == null) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.8), 0);
            mPlayer = MediaPlayer.create(this, R.raw.sound);
        }
        mPlayer.setOnCompletionListener(this);
        if(mPlayer.isPlaying()) {
            mPlayer.seekTo(0);
        }else {
            mPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayer.reset();
        mPlayer = null;
    }

    private void makeVibrate(){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(10000);
    }
}
