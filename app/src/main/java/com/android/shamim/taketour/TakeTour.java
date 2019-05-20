package com.android.shamim.taketour;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SHAMIM on 1/27/2018.
 */

public class TakeTour extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}