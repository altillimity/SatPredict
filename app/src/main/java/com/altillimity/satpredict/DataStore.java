package com.altillimity.satpredict;

import android.content.res.Resources;
import android.os.FileUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class DataStore {

    private File dataPath;

    public JSONObject data;
    public Map<String, String> satellites = new HashMap<String, String>();
    private Resources res;

    public DataStore(File folder, Resources res) {
        data = new JSONObject();
        dataPath = new File(folder, "data.json");
        this.res = res;
    }

    public void loadConfig() {
        if (dataPath.exists()) {
            try {
                data = (JSONObject) new JSONParser().parse(new FileReader(dataPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            satellites = (Map<String, String>) data.get("satellites");
        } else {
            InputStream is = res.openRawResource(R.raw.data);

            try {
                InputStream ins = res.openRawResource(R.raw.data);
                byte[] b = new byte[ins.available()];
                ins.read(b);
                PrintWriter pw = null;
                pw = new PrintWriter(dataPath);
                pw.write(new String(b));
                pw.flush();
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadConfig();
        }
    }

    public void saveConfig() {
        if (dataPath.exists())
            dataPath.delete();

        data.replace("satellites", satellites);

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(dataPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pw.write(data.toJSONString());
        pw.flush();
        pw.close();
    }

    public boolean initialSetupDone() {
        return (boolean) data.get("initialDone");
    }

    public void setInitialSetupDone(boolean value) {
        data.replace("initialDone", value);
    }

    public String getLongitude() {
        return (String) data.get("obsLon");
    }

    public void setLongitude(String value) {
        data.replace("obsLon", value);
    }

    public String getLatitude() {
        return (String) data.get("obsLat");
    }

    public void setLatitude(String value) {
        data.replace("obsLat", value);
    }
}