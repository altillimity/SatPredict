package com.altillimity.satpredict.activities;

import android.util.Pair;

import androidx.annotation.NonNull;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Satellite {
    private String satName;
    private String satTle1;
    private String satTle2;

    public Satellite(String name, String tle1, String tle2) {
        satName = name;
        satTle1 = tle1;
        satTle2 = tle2;
    }

    public native String getCurrentSatPos(String tle1, String tle2);

    public native String getSatPosAtTime(String tle1, String tle2, long time);

    public String getName() {
        return satName;
    }

    public String getTle1() {
        return satTle1;
    }

    public String getTle2() {
        return satTle2;
    }

    Double latitude = 0D, longitude = 0D, elevation = 0D;

    public void updateData() {
        String[] infos = getCurrentSatPos(satTle1, satTle2).split(":");
        latitude = Double.parseDouble(infos[0]);
        longitude = Double.parseDouble(infos[1]);
        elevation = Double.parseDouble(infos[2]);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getElevation() {
        return elevation;
    }

    public List<Pair<Long, Pair<Double, Double>>> predictOrbit(long startingTime, long endTime) {
        List<Pair<Long, Pair<Double, Double>>> result = new ArrayList<Pair<Long, Pair<Double, Double>>>();

        for (long i = startingTime; i <= endTime; i += 100) {
            String[] infos = getSatPosAtTime(satTle1, satTle2, i).split(":");
            Double plannedLatitude = Double.parseDouble(infos[0]);
            Double plannedLongitude = Double.parseDouble(infos[1]);
            result.add(Pair.create(i, Pair.create(plannedLatitude, plannedLongitude)));
        }

        return result;
    }

    @NonNull
    @Override
    public String toString() {
        JSONObject json = new JSONObject();

        json.put("name", satName);
        json.put("tle1", satTle1);
        json.put("tle2", satTle2);

        return json.toJSONString();
    }

    public static Satellite fromString(String input) {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(input);
        } catch (ParseException e) {
        }
        String name = (String) json.get("name");
        String tle1 = (String) json.get("tle1");
        String tle2 = (String) json.get("tle2");
        return new Satellite(name, tle1, tle2);
    }
}