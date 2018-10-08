package com.marcosalles.entertherow.models;

import com.google.android.gms.location.Geofence;

import java.util.UUID;

public class Fence {

    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;

    /**
     * @param geofenceId The Geofence's request ID.
     * @param latitude   Latitude of the Geofence's center in degrees.
     * @param longitude  Longitude of the Geofence's center in degrees.
     * @param radius     Radius of the geofence circle in meters.
     * @param expiration Geofence expiration duration.
     * @param transition Type of Geofence transition.
     */
    private Fence(String geofenceId, double latitude, double longitude, float radius,
                  long expiration, int transition) {
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }

    /**
     * @param latitude  Latitude of the Geofence's center in degrees.
     * @param longitude Longitude of the Geofence's center in degrees.
     */
    Fence(double latitude, double longitude) {
        this(UUID.randomUUID().toString(), latitude, longitude, 20F, Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT);
    }

    public String getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getRadius() {
        return mRadius;
    }

    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a Fence.
     *
     * @return A Geofence object.
     */
    public Geofence toGeofence() {
        return new Geofence.Builder()
                .setRequestId(mId)
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(mLatitude, mLongitude, mRadius)
                .setExpirationDuration(mExpirationDuration)
                .build();
    }
}