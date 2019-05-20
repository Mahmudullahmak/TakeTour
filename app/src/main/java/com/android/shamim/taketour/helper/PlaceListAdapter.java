package com.android.shamim.taketour.helper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.shamim.taketour.NearestPlace;
import com.android.shamim.taketour.PlaceDetail;
import com.android.shamim.taketour.R;
import com.android.shamim.taketour.pojjoclass.MyPlace;
import java.util.ArrayList;

/**
 * Created by shamim on 2/8/2018.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder>{

    private Context context;
    ArrayList<MyPlace> myPlaces;

    public PlaceListAdapter(NearestPlace context, ArrayList<MyPlace> myPlaces) {
        this.context = context;
        this.myPlaces = myPlaces;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.place_item,parent,false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, int position) {
      final MyPlace myPlace = myPlaces.get(position);
       holder.placename.setText(myPlace.getPlacename());
       holder.placeaddress.setText(myPlace.getVicinity());
    }

    @Override
    public int getItemCount() {
        return myPlaces.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private TextView placename, placeaddress;
        public PlaceViewHolder(View itemView) {
            super(itemView);
            placename =  itemView.findViewById(R.id.lpname);
            placeaddress = itemView.findViewById(R.id.lpaddr);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    MyPlace mPlace = myPlaces.get(position);
                    Intent intent = new Intent(context, PlaceDetail.class);
                    intent.putExtra("obj", mPlace);
                    context.startActivity(intent);
                }
            });

        }
    }
}
