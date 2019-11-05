package com.altillimity.satpredict.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.altillimity.satpredict.R;

public class AddSatActivity extends AppCompatActivity {
    EditText satNameField;
    EditText tle1Field;
    EditText tle2Field;

    Button addButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tle_add);

        satNameField = findViewById(R.id.satname);
        tle1Field = findViewById(R.id.tle1);
        tle2Field = findViewById(R.id.tle2);

        addButton = findViewById(R.id.btnadd);
        addButton.setOnClickListener(new AddButtonListener());
    }

    public class AddButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

                /*data.satellites.put(name.getText().toString(),
                        tle1.getText().toString() + ":" + tle2.getText().toString());
                data.saveConfig();
                startMainView();*/
        }
    }
}
