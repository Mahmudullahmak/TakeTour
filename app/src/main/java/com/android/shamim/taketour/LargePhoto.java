package com.android.shamim.taketour;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.shamim.taketour.pojjoclass.Moments;
import com.squareup.picasso.Picasso;

public class LargePhoto extends AppCompatActivity {
private ImageView imageView;
private TextView titleTextView;
ProgressBar progressBar;
Moments moment;
Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_photo);
        progressBar = findViewById(R.id.bigphoto);

         Intent i = getIntent();
           moment = (Moments) i.getSerializableExtra("obj");
           String title = moment.getCaptions();
           String photo = moment.getPhotourl();
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        imageView = (ImageView) findViewById(R.id.image);

       // Picasso.with(this).load(moment.getPhotourl()).into(imageView);
        progressBar.setVisibility(View.VISIBLE);
// Hide progress bar on successful load
        Picasso.with(this).load(moment.getPhotourl())
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });


        //  imageView.setImageBitmap(bitmap);
        progressBar.setVisibility(View.INVISIBLE) ;
    }
}
