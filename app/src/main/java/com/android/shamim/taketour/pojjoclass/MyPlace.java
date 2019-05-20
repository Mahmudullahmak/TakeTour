package com.android.shamim.taketour.pojjoclass;

import java.io.Serializable;

/**
 * Created by SAMIM on 2/8/2018.
 */

public class MyPlace implements Serializable{
  private String placeid;
  private String placename;
  private  String vicinity;
  private  double lat;
  private  double lon;

    public MyPlace() {
    }

    public MyPlace(String placeid, String placename, String vicinity, double lat, double lon) {
        this.placeid = placeid;
        this.placename = placename;
        this.vicinity = vicinity;
        this.lat = lat;
        this.lon = lon;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
