package com.altillimity.satpredict.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.altillimity.satpredict.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity {

    MapView mapView;

    Satellite sat;

    Context ctx;

    Timer updateTimer = new Timer();

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

        mapView.getOverlays().add(new TrajectoryOverlay(sat));

        this.setTitle(sat.getName());

        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        mapView.invalidate();
                    }
                });
            }
        }, 0, 1000);
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
