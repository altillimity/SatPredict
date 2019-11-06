package com.altillimity.satpredict.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.altillimity.satpredict.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity {

    MapView mapView;

    Satellite sat;

    Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_view);

        Intent intent = getIntent();
        sat = Satellite.fromString(intent.getStringExtra("sat"));
        sat.updateData();

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setOverScrollMode(2);
        IMapController mapController = mapView.getController();
        mapController.setZoom(5);
        GeoPoint startPoint = new GeoPoint(sat.getLatitude(), sat.getLongitude());
        mapController.setCenter(startPoint);

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        items.add(new OverlayItem("Current Position", "Latitude : " + sat.getLatitude() + "\nLongitude : " + sat.getLongitude(), new GeoPoint(sat.getLatitude(), sat.getLongitude()))); // Lat/Lon decimal degrees

        for (Pair<Long,Pair<Double,Double>> current : sat.predictOrbit(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + 100), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 3600*2)) {
            ReadableInstant instant = new DateTime(current.first * 1000, DateTimeZone.UTC);
            items.add(new OverlayItem("Position at UTC " + instant.get(DateTimeFieldType.dayOfMonth()) + "/" + instant.get(DateTimeFieldType.monthOfYear()) + "/"  + instant.get(DateTimeFieldType.year()) + " " + instant.get(DateTimeFieldType.clockhourOfDay()) + ":" + instant.get(DateTimeFieldType.minuteOfHour()), "Latitude : " + current.second.first.toString() + "\nLongitude : " + current.second.second.toString(), new GeoPoint(current.second.first, current.second.second))); // Lat/Lon decimal degrees
        }
//the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, ctx);
        mOverlay.setFocusItemsOnTap(true);

        mapView.getOverlays().add(mOverlay);

        this.setTitle(sat.getName());
    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
