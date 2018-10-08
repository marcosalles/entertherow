package com.marcosalles.entertherow.models;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.gms.maps.model.LatLng;
import com.marcosalles.entertherow.R;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TheRow {

    private final Set<Fence> fences;
    private final Set<LatLng> building;

    public TheRow(Set<Fence> fences, Set<LatLng> building) {
        this.fences = fences;
        this.building = building;
    }

    public static TheRow buildWith(Context context) {
        Set<Fence> fences = new HashSet<>();
        TypedArray centers = context.getResources().obtainTypedArray(R.array.the_row_centers);
        for (int i = 0; i < centers.length(); i += 2) {
            double lat = Double.valueOf(centers.getString(i));
            double lng = Double.valueOf(centers.getString(i + 1));

            Fence fence = new Fence(lat, lng);
            fences.add(fence);
        }
        centers.recycle();

        Set<LatLng> building = new HashSet<>();
        TypedArray corners = context.getResources().obtainTypedArray(R.array.the_row_corners);
        for (int i = 0; i < corners.length(); i += 2) {
            double lat = Double.valueOf(corners.getString(i));
            double lng = Double.valueOf(corners.getString(i + 1));
            LatLng corner = new LatLng(lat, lng);
            building.add(corner);
        }
        corners.recycle();

        return new TheRow(Collections.unmodifiableSet(fences), null);
    }

    public Set<Fence> getFences() {
        return fences;
    }

    public Set<LatLng> getBuilding() {
        return building;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("TheRow {\n")
                .append("  fences: \n")
                .append(fences)
                .append("  ,\n")
                .append("  building: \n")
                .append(building)
                .append("}")
                .toString();
    }
}