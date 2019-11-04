package com.altillimity.satpredict;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    void startMainView() {
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.sample_text);

        Button btn = findViewById(R.id.btnaddtle);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddView();
            }
        });

        t = new HelloThread();
        t.start();
    }

    void startAddView() {
        setContentView(R.layout.tle_add);
        Button btn = findViewById(R.id.btnadd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setInitialSetupDone(true);
                EditText name = findViewById(R.id.satname);
                EditText tle1 = findViewById(R.id.tle1);
                EditText tle2 = findViewById(R.id.tle2);
                data.satellites.put(name.getText().toString(),
                        tle1.getText().toString() + ":" + tle2.getText().toString());
                data.saveConfig();
                startMainView();
            }
        });
    }

    Thread t;
    TextView tv;
    DataStore data;

    MapView map = null;
    ItemizedOverlayWithFocus<OverlayItem> mOverlay = null;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new DataStore(getFilesDir()); data.loadConfig();
        /*
         *  if
         * (!data.initialSetupDone()) startAddView(); else startMainView();
         */
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // setting this before the layout is inflated is a good idea
        // it 'should' ensure that the map has a writable location for the map cache,
        // even without permissions
        // if no tiles are displayed, you can try overriding the cache path using
        // Configuration.getInstance().setCachePath
        // see also StorageUtils
        // note, the load method also sets the HTTP User Agent to your application's
        // package name, abusing osm's tile servers will get you banned based on this
        // string

        // inflate and create the map
        setContentView(R.layout.map_view);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setVerticalMapRepetitionEnabled(false);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setOverScrollMode(2);
        IMapController mapController = map.getController();
        mapController.setZoom(3);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        t = new HelloThread();
        t.start();
    }

    public void onResume() {
        super.onResume();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs =
        // PreferenceManager.getDefaultSharedPreferences(this);
        // Configuration.getInstance().load(this,
        // PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); // needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs =
        // PreferenceManager.getDefaultSharedPreferences(this);
        // Configuration.getInstance().save(this, prefs);
        map.onPause(); // needed for compass, my location overlays, v6.0.0 and up
    }

    public native String getCurrentSatPos(String tle1, String tle2);

    public class HelloThread extends Thread {
        public void run() {
            while (true) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        if (map.getOverlays().contains(mOverlay))
                            map.getOverlays().remove(mOverlay);

                        // your items
                        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

                        for (String sat : data.satellites.keySet()) {
                            String[] tle = data.satellites.get(sat).split(":");
                            String[] infos = getCurrentSatPos(tle[0], tle[1]).split(":");
                            OverlayItem newSat = new OverlayItem(sat, infos[2], new GeoPoint(Double.parseDouble(infos[0]), Double.parseDouble(infos[1])));
                            System.out.println(infos[0] + " - " + infos[1] + " " + sat);
                            items.add(newSat); // Lat/Lon decimal degrees
                        }

                        // the overlay
                        mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                    @Override
                                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                                        return true;
                                    }

                                    @Override
                                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                                        return false;
                                    }
                                }, ctx);
                        mOverlay.setFocusItemsOnTap(true);
                        map.getOverlays().add(mOverlay);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
