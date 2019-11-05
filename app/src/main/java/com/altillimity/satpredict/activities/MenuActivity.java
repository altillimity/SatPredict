package com.altillimity.satpredict.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.altillimity.satpredict.DataStore;
import com.altillimity.satpredict.R;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public static DataStore DATA;

    Activity thisAct = this;

    ArrayList<Satellite> satellites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DATA = new DataStore(getFilesDir());
        DATA.loadConfig();

        setContentView(R.layout.menu_view);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menuRecycler);

        satellites = new ArrayList<Satellite>();

        for (String sat : DATA.satellites.keySet()) {
            String[] tle = DATA.satellites.get(sat).split(":");
            satellites.add(new Satellite(sat, tle[0], tle[1]));
        }


        SatAdapter adapter = new SatAdapter(satellites);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.setTitle("Satellites");
    }

    public class SatAdapter extends
            RecyclerView.Adapter<SatAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView nameTextView;
            public TextView infoTextView;
            public Button buttonMap;

            public ViewHolder(View itemView) {
                super(itemView);

                nameTextView = (TextView) itemView.findViewById(R.id.menu_text);
                infoTextView = (TextView) itemView.findViewById(R.id.textViewInfo);
                buttonMap = (Button) itemView.findViewById(R.id.buttonMap);
            }
        }

        private List<Satellite> mSatellite;

        public SatAdapter(List<Satellite> sats) {
            mSatellite = sats;
        }

        @Override
        public SatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.menulayout, parent, false);

            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SatAdapter.ViewHolder viewHolder, int position) {

            final Satellite satellite = mSatellite.get(position);

            TextView textView = viewHolder.nameTextView;
            TextView info = viewHolder.infoTextView;

            textView.setText(satellite.getName());
            satellite.updateData();
            info.setText("Latitude : "+satellite.getLatitude().toString()+"°\nLongitude : "+satellite.getLontitude().toString()+"°\nElevation : "+satellite.getElevation().toString()+"°");

            Button btn = viewHolder.buttonMap;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(thisAct, MapActivity.class);

                    intent.putExtra("latitude", satellite.getLatitude());
                    intent.putExtra("longitude", satellite.getLontitude());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return satellites.size();
        }
    }
}
