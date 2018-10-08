package com.marcosalles.entertherow.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.marcosalles.entertherow.managers.CustomNotificationManager;
import com.marcosalles.entertherow.managers.GeofenceManager;

import static com.marcosalles.entertherow.MainActivity.TAG;

public class GeofenceTransitionsIntentService extends IntentService {

    private CustomNotificationManager notifications;

    public GeofenceTransitionsIntentService() {
        this("Enter The Row Geofencing intent service");
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.notifications = new CustomNotificationManager(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Caught intent!");
        GeofenceManager.instance().notifyIfInsideBounds(this.notifications);
    }
}
