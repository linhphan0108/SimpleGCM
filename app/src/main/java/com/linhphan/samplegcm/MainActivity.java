package com.linhphan.samplegcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


/**
 * tool for simulating a test server: https://chrome.google.com/webstore/detail/dhc-resthttp-api-client/aejoelaoggembcahagimdiliamlcdmfm
 */
public class MainActivity extends AppCompatActivity {

    // Resgistration Id from GCM
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    private SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String GCM_SENDER_ID = "480450616857";//project number
    private static final String WEB_SERVER_URL = "YOUR_WER_SERVER_URL";

    private GoogleCloudMessaging gcm;
    private Button mBtnRegistration;
    private TextView mTxtRegistrationId;

    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MSG_REGISTER_WITH_GCM = 101;
    protected static final int MSG_REGISTER_WEB_SERVER = 102;
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;
    private String gcmRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnRegistration = (Button) findViewById(R.id.register_gcmserver);
        mTxtRegistrationId = (TextView) findViewById(R.id.regId);
        mBtnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check device for Play Services APK.
                if (isGoogelPlayInstalled()) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

                    // Read saved registration id from shared preferences.
                    gcmRegId = getSharedPreferences().getString(PREF_GCM_REG_ID, "");
                    Log.e(getClass().getName(), "registration id: "+ gcmRegId);
                    if (TextUtils.isEmpty(gcmRegId)) {
                        handler.sendEmptyMessage(MSG_REGISTER_WITH_GCM);
                    }
                }
            }
        });
    }

    private boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, ACTION_PLAY_SERVICES_DIALOG);
            }else{
                Toast.makeText(getApplicationContext(), "Google Play Service is not installed", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = getApplicationContext().getSharedPreferences(
                    "AndroidSRCDemo", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public void saveInSharedPref(String result) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_GCM_REG_ID, result);
        editor.apply();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_WITH_GCM:
                    new GCMRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER:
                    //send sender id to our server
//                    new WebServerRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER_SUCCESS:
                    Toast.makeText(getApplicationContext(),
                            "registered with web server", Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "registration with web server failed",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * registering sender id to GCM server.
     */
    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (gcm == null && isGoogelPlayInstalled()){
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }

            try {
                gcmRegId = gcm.register(GCM_SENDER_ID);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
                Toast.makeText(getApplicationContext(), "registered with GCM", Toast.LENGTH_LONG).show();
                mTxtRegistrationId.setText(s);
                saveInSharedPref(s);
                Log.e(getClass().getName(), "registration id: "+ s);
                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER);
            }
        }
    }
}
