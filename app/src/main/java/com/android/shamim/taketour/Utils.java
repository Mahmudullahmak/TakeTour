package com.android.shamim.taketour;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SAMIM on 1/27/2018.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;


    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}