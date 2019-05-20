package com.android.shamim.taketour.helper;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.shamim.taketour.pojjoclass.HasValueParePlaceType;

import java.util.ArrayList;

/**
 * Created by SAMIM on 2/8/2018.
 */
public class KeyValueSpinner implements SpinnerAdapter {
    Context context;
    ArrayList<HasValueParePlaceType> aiiValues;
HasValueParePlaceType ff;
    public KeyValueSpinner(Context context ,ArrayList<HasValueParePlaceType> aiiValues){
        this.context =context;
        this.aiiValues = aiiValues;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return aiiValues.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return aiiValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    //Note:-Create this two method getIDFromIndex and getIndexByID
    public String getIDFromIndex(int Index) {
        return    aiiValues.get(Index).getTypeKey();
    }

    public int getIndexByID(String ID) {
        for(int i=0;     i < aiiValues.size(); i++) {
            if(aiiValues.get(i).getTypeKey() == ID){
            return i;
        }
    }
        return -1;
}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        textview.setText(aiiValues.get(position).getKeyValue());

        return textview;
    }

    @Override
    public int getViewTypeCount() {
        return android.R.layout.simple_spinner_item;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        textview.setText(aiiValues.get(position).getKeyValue());

        return textview;
    }
}