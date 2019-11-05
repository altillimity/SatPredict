package com.altillimity.satpredict.activities;

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

    public String getName() {
        return satName;
    }

    public String getTle1() {
        return satTle1;
    }

    public String getTle2() {
        return satTle2;
    }

    Double latitude = 0D, lontitude = 0D, elevation = 0D;

    public void updateData() {
        String[] infos = getCurrentSatPos(satTle1, satTle2).split(":");
        latitude = Double.parseDouble(infos[0]);
        lontitude = Double.parseDouble(infos[1]);
        elevation = Double.parseDouble(infos[2]);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLontitude() {
        return lontitude;
    }

    public Double getElevation() {
        return elevation;
    }
}