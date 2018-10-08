package com.marcosalles.entertherow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.marcosalles.entertherow.managers.GeofenceManager;

public class MainActivity extends Activity {

    public static final String TAG = "Enter The Row";
    private GeofenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {
            this.finish();
            return;
        }
        manager = new GeofenceManager(this);
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Google Play services is available.");
            }
            return true;
        }
        Log.e(TAG, "Google Play services is unavailable.");
        return false;
    }
}
