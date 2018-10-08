package com.marcosalles.entertherow.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * this point-contained-in-boundaries logic has been found online
 */
public class BoundaryCheck {
    public boolean pointIsInsideBoundaries(LatLng point, List<LatLng> boundaries) {
        int crossings = 0;

        int count = boundaries.size();
        // for each edge
        for (int i = 0; i < count; i++) {
            LatLng cornerA = boundaries.get(i);
            int j = i + 1;
            if (j >= count) {
                j = 0;
            }
            LatLng cornerB = boundaries.get(j);
            if (this.rayCastIsCrossing(point, cornerA, cornerB)) {
                crossings++;
            }
        }
        return (crossings % 2 == 1);
    }

    private boolean rayCastIsCrossing(LatLng point, LatLng cornerA, LatLng cornerB) {
        double px = point.longitude;
        double py = point.latitude;
        double ax = cornerA.longitude;
        double ay = cornerA.latitude;
        double bx = cornerB.longitude;
        double by = cornerB.latitude;
        if (ay > by) {
            ax = cornerB.longitude;
            ay = cornerB.latitude;
            bx = cornerA.longitude;
            by = cornerA.latitude;
        }
        if (px < 0) {
            px += 360;
        }
        ;
        if (ax < 0) {
            ax += 360;
        }
        ;
        if (bx < 0) {
            bx += 360;
        }
        ;

        if (py == ay || py == by) py += 0.00000001;
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) return false;
        if (px < Math.min(ax, bx)) return true;

        double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.MAX_VALUE;
        double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.MAX_VALUE;
        return blue >= red;
    }
}