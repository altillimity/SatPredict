package com.altillimity.satpredict;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.Map;

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
                data.satellites.put(name.getText().toString(), tle1.getText().toString() + ":" + tle2.getText().toString());
                data.saveConfig();
                startMainView();
            }
        });
    }

    Thread t;
    TextView tv;
    DataStore data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = new DataStore(getFilesDir());
        data.loadConfig();
        if (!data.initialSetupDone())
            startAddView();
        else
            startMainView();

    }

    public native String testJNI(String tle1, String tle2);

    public class HelloThread extends Thread {
        public void run() {
            while (true) {
                runOnUiThread(new Runnable()
                {
                    public void run() {
                        String toShow = "";

                        for(String sat : data.satellites.keySet()) {
                            String[] tle = data.satellites.get(sat).split(":");
                            toShow += sat + " : " + testJNI(tle[0], tle[1]) + '\n';
                        }
                        tv.setText(toShow);
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
