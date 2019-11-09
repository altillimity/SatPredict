package com.altillimity.satpredict.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.altillimity.satpredict.DataStore;
import com.altillimity.satpredict.R;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public static DataStore DATA;

    Activity thisAct = this;

    ArrayList<Satellite> satellites;

    RecyclerView recyclerView;
    SatAdapter adapter;

    Timer timer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DATA = new DataStore(getFilesDir());
        DATA.loadConfig();

        setContentView(R.layout.menu_view);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        recyclerView = (RecyclerView) findViewById(R.id.menuRecycler);

        satellites = new ArrayList<Satellite>();

        for (String sat : DATA.satellites.keySet()) {
            String[] tle = DATA.satellites.get(sat).split(":");
            satellites.add(new Satellite(sat, tle[0], tle[1], Double.valueOf(DATA.getLongitude()),
                    Double.valueOf(DATA.getLatitude())));
        }

        adapter = new SatAdapter(satellites);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);

        this.setTitle("Satellites");

        Button btn = (Button) findViewById(R.id.buttonAddSat);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisAct, AddSatActivity.class);
                startActivity(intent);
            }
        });

        Button btn2 = (Button) findViewById(R.id.buttonConfig);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisAct, ConfigActivity.class);
                startActivity(intent);
            }
        });
    }

    public class SatAdapter extends RecyclerView.Adapter<SatAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView nameTextView;
            public TextView infoTextView;
            public Button buttonMap;

            public ViewHolder(View itemView) {
                super(itemView);

                nameTextView = (TextView) itemView.findViewById(R.id.menu_text);
                infoTextView = (TextView) itemView.findViewById(R.id.textViewInfo);
                buttonMap = (Button) itemView.findViewById(R.id.buttonMap);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final int index = getAdapterPosition();
                        new AlertDialog.Builder(thisAct).setTitle(satellites.get(index).getName())
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DATA.satellites.remove(satellites.get(index).getName());
                                        satellites.remove(index);
                                        DATA.saveConfig();

                                        // Used to recompute the layout as well
                                        adapter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                        return false;
                    }
                });
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
            final TextView info = viewHolder.infoTextView;

            textView.setText(satellite.getName());
            satellite.updateData();
            info.setText("Latitude : " + satellite.getLatitude().toString() + "°\nLongitude : "
                    + satellite.getLongitude().toString() + "°\nElevation : " + satellite.getElevation().toString()
                    + "°");

            Button btn = viewHolder.buttonMap;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(thisAct, MapActivity.class);
                    intent.putExtra("sat", satellite.toString());
                    startActivity(intent);
                }
            });

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    satellite.updateData();
                    info.setText("Latitude : " + satellite.getLatitude().toString() + "°\nLongitude : "
                            + satellite.getLongitude().toString() + "°\nElevation : "
                            + satellite.getElevation().toString() + "°");
                }
            }, 0, 1000);

        }

        @Override
        public int getItemCount() {
            return satellites.size();
        }
    }
}
