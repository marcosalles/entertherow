package com.marcosalles.entertherow.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;
import com.marcosalles.entertherow.managers.CustomNotificationManager;
import com.marcosalles.entertherow.models.BoundaryCheck;
import com.marcosalles.entertherow.models.TheRow;

import static com.marcosalles.entertherow.MainActivity.TAG;

public class GeofenceTransitionsIntentService extends IntentService {

    public static final String LAST_LOCATION_IS_INSIDE = "lastLocation.isInside";
    public static final String LAST_LOCATION_LAT = "lastLocation.lat";
    public static final String LAST_LOCATION_LNG = "lastLocation.lng";
    private CustomNotificationManager notifications;
    private TheRow theRow;
    private BoundaryCheck boundaryChecker;
    private SharedPreferences prefs;

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
        this.theRow = TheRow.buildWith(this);
        this.boundaryChecker = new BoundaryCheck();
        this.prefs = this.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Caught intent!");

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            return;
        }


        int transition = event.getGeofenceTransition();
        boolean lastPositionWasInsideBuilding = lastPositionWasInsideBuilding();
        LatLng currentPosition = getCurrentPosition();
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER && !lastPositionWasInsideBuilding) {
            if (currentPosition == null) {
                // if I don't have the users current position stored,
                // I might as well assume they are inside the building
                storeEntryAndNotify();
                return;
            }
            boolean isInsideBoundaries = boundaryChecker.pointIsInsideBoundaries(currentPosition, theRow.getBuilding());
            if (isInsideBoundaries) storeEntryAndNotify();
        }
        if (transition == Geofence.GEOFENCE_TRANSITION_EXIT && lastPositionWasInsideBuilding) {
            if (currentPosition == null) {
                storeEntryIntoBuilding(false);
            } else {
                if (boundaryChecker.pointIsInsideBoundaries(currentPosition, theRow.getBuilding())) {
                    storeEntryIntoBuilding(true);
                }
                else {
                    storeEntryIntoBuilding(false);
                }
            }
        }
    }

    @NonNull
    private LatLng getCurrentPosition() {
        String lat = prefs.getString(LAST_LOCATION_LAT, null);
        String lng = prefs.getString(LAST_LOCATION_LNG, null);
        if (lat == null || lng == null) return null;
        return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
    }

    private boolean lastPositionWasInsideBuilding() {
        return prefs.getBoolean(LAST_LOCATION_IS_INSIDE, false);
    }

    public void storeEntryAndNotify() {
        this.storeEntryIntoBuilding(true);
        this.toast(this, "You've just entered The Row!", Toast.LENGTH_LONG);
        notifications.send("Enter The Row!", "You've just entered The Row!");
    }

    private void storeEntryIntoBuilding(boolean isInside) {
        SharedPreferences.Editor editor = this.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LAST_LOCATION_IS_INSIDE, isInside);
        editor.commit();
    }

    private void toast(final Context context, final String message, final int length) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, length).show();
            }
        });
    }


}
