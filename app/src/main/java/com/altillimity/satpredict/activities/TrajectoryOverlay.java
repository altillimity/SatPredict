package com.altillimity.satpredict.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Pair;

import com.altillimity.satpredict.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrajectoryOverlay extends Overlay {

    final Satellite satellite;

    public TrajectoryOverlay(Satellite sat) {
        satellite = sat;
    }

    @Override
    public void draw(Canvas pCanvas, MapView pMapView, boolean pShadow) {
        final Projection pj = pMapView.getProjection();

        Paint paint = new Paint();

        satellite.updateData();

        Bitmap bm = BitmapFactory.decodeResource(pMapView.getResources(), R.drawable.saticon);

        pMapView.getProjection().save(pCanvas, false, false);

        List<Pair<Long, Pair<Double, Double>>> orbit = satellite.predictOrbit(
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 3600 * 2);

        for (int i = 0; i < orbit.size() - 1; i++) {
            Pair<Long, Pair<Double, Double>> pos1 = orbit.get(i);
            Pair<Long, Pair<Double, Double>> pos2 = orbit.get(i + 1);
            if (Math.sqrt(Math.pow(pos1.second.second - pos2.second.second, 2)
                    + Math.pow(pos1.second.first - pos2.second.first, 2)) < 100)
                pCanvas.drawLine(pj.getLongPixelXFromLongitude(pos1.second.second),
                        pj.getLongPixelYFromLatitude(pos1.second.first),
                        pj.getLongPixelXFromLongitude(pos2.second.second),
                        pj.getLongPixelYFromLatitude(pos2.second.first), paint);
        }

        pCanvas.drawBitmap(bm, pj.getLongPixelXFromLongitude(satellite.getLongitude()) - bm.getWidth() / 2,
                pj.getLongPixelYFromLatitude(satellite.getLatitude()) - bm.getHeight() / 2, paint);

        pCanvas.drawText("Latitude : " + satellite.getLatitude().toString() + "°", 10, 20, paint);
        pCanvas.drawText("Longitude : " + satellite.getLongitude().toString() + "°", 10, 40, paint);
        pCanvas.drawText("Elevation : " + satellite.getElevation().toString() + "°", 10, 60, paint);
        pMapView.getProjection().restore(pCanvas, false);
    }
}
