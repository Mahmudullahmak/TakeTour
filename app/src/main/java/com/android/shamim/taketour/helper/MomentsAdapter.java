package com.android.shamim.taketour.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.shamim.taketour.R;
import com.android.shamim.taketour.pojjoclass.Moments;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SAMIM on 2/3/2018.
 */


public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.MomentsHoder>{

    ArrayList<Moments> list;
    Context context;

    public MomentsAdapter(ArrayList<Moments> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MomentsHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.moments_items,parent,false);
        MomentsHoder momentsHoder = new MomentsHoder(view);


        return momentsHoder;
    }

    @Override
    public void onBindViewHolder(final MomentsHoder holder, int position) {
        Moments mylist = list.get(position);
        holder.imageCaptionTextView.setText(mylist.getCaptions());
        //Picasso.with(context).load(mylist.getPhotourl()).into(holder.imageView);

        // Show progress bar
        holder.progressBar.setVisibility(View.VISIBLE);
// Hide progress bar on successful load
        Picasso.with(context).load(mylist.getPhotourl())
                .into(holder.imageView, new com.squareup.picasso.Callback() {
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

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }

        return arr;

    }

    class MomentsHoder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView imageNameTextView, imageCaptionTextView;
        public ProgressBar progressBar;
        public MomentsHoder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
             imageCaptionTextView = (TextView) itemView.findViewById(R.id.ImageCaptionTextView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

}


