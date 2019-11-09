package com.altillimity.satpredict.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.altillimity.satpredict.R;

public class ConfigActivity extends AppCompatActivity implements LocationListener {
    EditText latField;
    EditText lonField;

    Button locateButton, saveButton;

    Switch dynamicLocationSwitch;

    Activity thisAct = this;

    LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.config_view);
        this.setTitle("Settings");

        latField = findViewById(R.id.editTextLatitude);
        lonField = findViewById(R.id.editTextLongitude);

        latField.setText(MenuActivity.DATA.getLatitude());
        lonField.setText(MenuActivity.DATA.getLongitude());

        dynamicLocationSwitch = findViewById(R.id.switchDynamicLocation);

        locateButton = findViewById(R.id.buttonAutoLocate);
        locateButton.setOnClickListener(new LocateButtonListener());

        saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new SaveButtonListener());
    }

    public class LocateButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(thisAct, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(thisAct, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
            try {
                Toast.makeText(thisAct, "Getting your current location...", Toast.LENGTH_SHORT).show();
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, ConfigActivity.this);
            }
            catch(SecurityException e) {
                e.printStackTrace();
                Toast.makeText(thisAct, "Error while getting your location!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SaveButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                Double.parseDouble(latField.getText().toString());
                Double.parseDouble(lonField.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(thisAct, "Error while saving!", Toast.LENGTH_SHORT).show();
                return;
            }

            MenuActivity.DATA.setLatitude(latField.getText().toString());
            MenuActivity.DATA.setLongitude(lonField.getText().toString());
            MenuActivity.DATA.saveConfig();

            Toast.makeText(thisAct, "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lonField.setText(String.valueOf(location.getLongitude()));
        latField.setText(String.valueOf(location.getLatitude()));
        locationManager.removeUpdates(ConfigActivity.this);
        Toast.makeText(thisAct, "Done!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please enable location to enable auto filling your longitude and latitude!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
