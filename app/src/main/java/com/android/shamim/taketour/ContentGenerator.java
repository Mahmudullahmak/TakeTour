package com.android.shamim.taketour;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shamim on 2/7/2018.
 */

public class ContentGenerator {
public  static HashMap<String,String> getPlaceType2() {
    HashMap<String,String>placeType = new HashMap<String, String>();

    placeType.put("hospital","Hospital");
    placeType.put("police","Police Station");
    placeType.put("post_office","Post office");
    placeType.put("school","School");
    placeType.put("shopping_mall","Shopping Mall");
    placeType.put("mosque","Masjid");
    placeType.put("stadium","Stadium");
    placeType.put("restaurant","Restaurant");
    placeType.put("bus_station","Bus Station");
    placeType.put("bank","Bank");
    placeType.put("atm","ATM Booth");


    return placeType;
}
    public static ArrayList<String> getPlaceType(){
        ArrayList<String>ptype = new ArrayList<>();
        ptype.add("Hospital");
        ptype.add("Police Station");
        ptype.add("Post office");
        ptype.add("School");
        ptype.add("Shopping Mall");
        ptype.add("Mosque");
        ptype.add("Stadium");
        ptype.add("Stadium");
        ptype.add("Restaurant");
        ptype.add("Bus Station");
        ptype.add("Bank");
        ptype.add("ATM Booth");
        return ptype;
    }

        public static ArrayList<String> generateListOfarea(){
            ArrayList<String>pkm = new ArrayList<>();
            pkm.add("0.5km");
            pkm.add("1km");
            pkm.add("1.5km");
            pkm.add("2km");
            pkm.add("2.5km");
            pkm.add("3km");
            pkm.add("4km");
            pkm.add("5km");
            pkm.add("10km");
            return pkm;
        }
}
