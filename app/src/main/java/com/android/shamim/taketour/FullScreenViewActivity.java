package com.android.shamim.taketour;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.android.shamim.taketour.pojjoclass.Utils;
import com.android.shamim.taketour.helper.FullScreenImageAdapter;

public class FullScreenViewActivity extends Activity{

    private Utils utils;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        utils = new Utils(getApplicationContext());

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
                utils.getFilePaths());

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}