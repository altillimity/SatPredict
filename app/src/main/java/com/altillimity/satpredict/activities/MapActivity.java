package com.altillimity.satpredict.activities;

import android.app.Activity;
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
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    MapView mapView;

    Double latitude, longitude;

    Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_view);

        Intent intent = getIntent();
        latitude =  intent.getDoubleExtra("latitude", 0D);
        longitude =  intent.getDoubleExtra("longitude", 0D);

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
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", startPoint)); // Lat/Lon decimal degrees

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
