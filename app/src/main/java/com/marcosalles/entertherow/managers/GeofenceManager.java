package com.marcosalles.entertherow.managers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.marcosalles.entertherow.models.Fence;
import com.marcosalles.entertherow.models.TheRow;
import com.marcosalles.entertherow.services.GeofenceTransitionsIntentService;

import java.util.ArrayList;
import java.util.List;

import static com.marcosalles.entertherow.MainActivity.TAG;

public class GeofenceManager extends LocationCallback implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int PENDING_INTENT_REQUEST_CODE = 0;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private static GeofenceManager instance;

    private Activity context;
    private GoogleApiClient apiClient;
    private TheRow theRow;
    private PendingIntent pendingIntent;
    private LocationRequest locationRequest;
    private LatLng lastLocation;

    private GeofenceManager() {
    }

    public static GeofenceManager instance() {
        if (instance == null) instance = new GeofenceManager();
        return instance;
    }

    public GeofenceManager init(Activity context) {
        this.context = context;
        this.apiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.apiClient.connect();
        this.theRow = TheRow.buildWith(context);
        Log.d(TAG, "Initialized manager");
        return this;
    }

    public void notifyIfInsideBounds(CustomNotificationManager notifications) {
        this.toast(notifications.getContext(), "You've just entered The Row!", Toast.LENGTH_LONG);
        notifications.send("Enter The Row!", "You've just entered The Row!");
    }

    private void updateGeofences() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        List<Geofence> geofencesToAdd = new ArrayList<>();
        for (Fence geofence : theRow.getFences()) {
            geofencesToAdd.add(geofence.toGeofence());
        }

        PendingIntent intent = this.generateIntent();
        LocationServices.GeofencingApi.addGeofences(apiClient, geofencesToAdd, intent);
        this.toast(context, "Starting geofencing service", Toast.LENGTH_SHORT);

        /*
        // I failed this one, went back to the older deprecated version. Need more time to study and test
        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofences(geofencesToAdd)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        this.removeGeofences(geofencingClient);
        geofencingClient.addGeofences(request, this.generateIntent());*/
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

    private PendingIntent generateIntent() {
        if (pendingIntent == null) {
            Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
            ContextCompat.startForegroundService(context, intent);
            pendingIntent = PendingIntent.getService(context, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    private void requestLocation() {
        if (locationRequest != null) return;

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5000)
                .setInterval(10000);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        client.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
    }

    private void removeGeofences(GeofencingClient client) {
        if (pendingIntent != null) client.removeGeofences(pendingIntent);
    }

    /* START - GoogleApiClient.ConnectionCallbacks */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to api");
        this.requestLocation();
        this.updateGeofences();
        context.finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection to api suspended");
    }
    /* END - GoogleApiClient.ConnectionCallbacks */

    /* START - GoogleApiClient.OnConnectionFailedListener */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection to api failed");
    }
    /* END - GoogleApiClient.OnConnectionFailedListener */

    /* START - LocationCallback */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        Location lastLocation = locationResult.getLastLocation();
        this.lastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }
    /* END - LocationCallback */

}
