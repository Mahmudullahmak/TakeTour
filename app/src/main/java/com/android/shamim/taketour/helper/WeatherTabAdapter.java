package com.android.shamim.taketour.helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.shamim.taketour.tab.CurrentWeatherFragment;
import com.android.shamim.taketour.tab.ForecastWeatherFragment;

/**
 * Created by SAMIM on 1/26/2018.
 */

public class WeatherTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
Bundle tb;
    public WeatherTabAdapter(FragmentManager fm, int NumOfTabs, Bundle b) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.tb=b;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0: // Fragment # 0 - This will show FirstFragment
                return CurrentWeatherFragment.newInstance(tb);
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return ForecastWeatherFragment.newInstance(tb);

            default:
                return null;
        }

    }












/*

    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CurrentWeatherFragment tab1 = new CurrentWeatherFragment();
                return tab1;
            case 1:
                ForecastWeatherFragment tab2 = new ForecastWeatherFragment();
                return tab2;
           default:
                return null;
        }
    }*/

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}