package com.altillimity.satpredict.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

    Activity thisAct = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tle_add);

        satNameField = findViewById(R.id.satname);
        tle1Field = findViewById(R.id.tle1);
        tle2Field = findViewById(R.id.tle2);

        addButton = findViewById(R.id.btnadd);
        addButton.setOnClickListener(new AddButtonListener());

        this.setTitle("Add satellite");
    }

    public class AddButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(MenuActivity.DATA.satellites.containsKey(satNameField.getText().toString())) {
                new AlertDialog.Builder(thisAct).setTitle("Duplicate")
                        .setMessage("Another satellite with this name already exists!").setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
                return;
            }

            try {
                new Satellite(satNameField.getText().toString(), tle1Field.getText().toString(),
                        tle2Field.getText().toString()).updateData();
            } catch (NumberFormatException e) {
                new AlertDialog.Builder(thisAct).setTitle("TLE parsing error")
                        .setMessage("Make sure you entered it right!").setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
                return;
            }

            MenuActivity.DATA.satellites.put(satNameField.getText().toString(),
                    tle1Field.getText().toString() + ":" + tle2Field.getText().toString());
            MenuActivity.DATA.saveConfig();
            Intent intent = new Intent(thisAct, MenuActivity.class);
            startActivity(intent);
        }
    }
}
