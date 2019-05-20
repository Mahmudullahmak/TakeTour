package com.android.shamim.taketour.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.shamim.taketour.GeofenceList;
import com.android.shamim.taketour.R;
import com.android.shamim.taketour.pojjoclass.Geofenc;

import java.util.ArrayList;

/**
 * Created by shamim on 2/7/2018.
 */

public class GeofenceAdapter extends RecyclerView.Adapter<GeofenceAdapter.GeoviewHolder>{

    private Context context;
    private ArrayList<Geofenc> geofencs;
    private int count = 0;

    public GeofenceAdapter(Context context, ArrayList<Geofenc> geofencs) {
        this.context=context;
        this.geofencs=geofencs;
    }

    @Override
    public GeoviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_fencing,parent,false);
        return new GeoviewHolder(v);
    }


    @Override
    public void onBindViewHolder(final GeoviewHolder holder, int position) {
        final Geofenc geofenc = geofencs.get(position);

        holder.fencgeotitleTv.setText(geofenc.getFenceName());
        holder.gGeolatTv.setText("Lat: " + String.valueOf(geofenc.getFencelat()));
        holder.GeolonTv.setText("Lon: " + String.valueOf(geofenc.getFenceLon()));
        holder.optionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.delete:
                                try {
                                    ((GeofenceList) v.getContext()).deleteRecord(geofenc);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.fencgeotitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){


                            case R.id.delete:
                                try {
                                    ((GeofenceList) v.getContext()).deleteRecord(geofenc);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.gGeolatTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu3);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                try {
                                    ((GeofenceList) v.getContext()).deleteRecord(geofenc);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        holder.GeolonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.delete:
                                try {
                                    ((GeofenceList) v.getContext()).deleteRecord(geofenc);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(geofencs.size()==0){
                arr = 0;
            }
            else{
                arr=geofencs.size();     }
        }catch (Exception e){       }
        return arr;
    }

    public class GeoviewHolder extends RecyclerView.ViewHolder {

        TextView fencgeotitleTv, gGeolatTv,GeolonTv, optionDigit;
        public GeoviewHolder(View itemView) {
            super(itemView);
            fencgeotitleTv = itemView.findViewById(R.id.geofencname);
            gGeolatTv = itemView.findViewById(R.id.latfec);
            GeolonTv = itemView.findViewById(R.id.lonfec);
            optionDigit = itemView.findViewById(R.id.optionDigit);
        }
    }
}
