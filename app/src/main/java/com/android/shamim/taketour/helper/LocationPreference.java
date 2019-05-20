package com.android.shamim.taketour.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by SAMIM on 1/30/2018.
 */

public class LocationPreference {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String LOCATION_PREFFERENCE_NAME = "takeTourLocation";

    public LocationPreference(Context context) {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(LOCATION_PREFFERENCE_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }
    public void saveLocation(String lat, String lon){
        editor.putString("lat",lat);
        editor.putString("lon",lon);
        editor.commit();
    }
    public String getLaetSaveLatitute(){
        return sharedPreferences.getString("lat",null);
    }
    public String getLaetSaveLongitute(){
        return  sharedPreferences.getString("lon",null);
    }
    public void resetLocation(){
        editor.clear();
        editor.commit();
    }
}
