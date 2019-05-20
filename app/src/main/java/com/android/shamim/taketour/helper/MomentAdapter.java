package com.android.shamim.taketour.helper;
import android.content.Context;
        import android.content.Intent;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.shamim.taketour.AddEvent;
import com.android.shamim.taketour.AddGeofencing;
import com.android.shamim.taketour.EventDetail;
import com.android.shamim.taketour.EventList;
import com.android.shamim.taketour.LargePhoto;
import com.android.shamim.taketour.MomentDetail;
import com.android.shamim.taketour.R;
        import com.android.shamim.taketour.pojjoclass.Moments;
        import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMIM on 2/3/2018.
 */

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MyHoder>{

    ArrayList<Moments> list;
    Context context;

    public MomentAdapter(ArrayList<Moments> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moments_items, parent, false);

        MyHoder viewHolder = new MyHoder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyHoder holder, int position) {
        final  Moments moment= list.get(position);

        //Loading image from Picaso library.
        holder.caption.setText(moment.getCaptions());
       // Picasso.with(context).load(moment.getPhotourl()).into(holder.gallerythumb);

        // Show progress bar
        holder.progressBar.setVisibility(View.VISIBLE);
// Hide progress bar on successful load
        Picasso.with(context).load(moment.getPhotourl())
                .into(holder.gallerythumb, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.progressBar != null) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });




        holder.gallerythumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LargePhoto.class);
                i.putExtra("obj", moment);
                context.startActivity(i);
            }
        });

        holder.optionDigit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit3);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.edit:

                                try {
                                    ((MomentDetail) v.getContext()).editMomentDialog(moment);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;
                            case R.id.delete:
                                try {
                                    ((MomentDetail) v.getContext()).deleteRecord(moment);
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
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();     }
        }catch (Exception e){       }
        return arr;
    }

    class MyHoder extends RecyclerView.ViewHolder{

        ImageView gallerythumb;
        TextView caption;
        TextView optionDigit3;
        ProgressBar progressBar;

        public MyHoder(View itemView) {
            super(itemView);
            gallerythumb = (ImageView) itemView.findViewById(R.id.imageView);
            caption = (TextView) itemView.findViewById(R.id.ImageCaptionTextView);
            optionDigit3 = itemView.findViewById(R.id.optionDigit);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

}
