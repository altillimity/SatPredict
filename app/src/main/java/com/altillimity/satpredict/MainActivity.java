package com.altillimity.satpredict;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.altillimity.satpredict.activities.AddSatActivity;
import com.altillimity.satpredict.activities.MapActivity;
import com.altillimity.satpredict.activities.MenuActivity;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public static DataStore DATA;

    MainActivity thisAct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DATA = new DataStore(getFilesDir());
        DATA.loadConfig();

        setContentView(R.layout.activity_main);

        findViewById(R.id.btnaddtle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisAct, AddSatActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisAct, MapActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnmenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisAct, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
