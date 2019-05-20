package com.android.shamim.taketour;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.EventAdapter;
import com.android.shamim.taketour.helper.GalleryAdapter;
import com.android.shamim.taketour.pojjoclass.Events;
import com.android.shamim.taketour.pojjoclass.Moments;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventGallery extends AppCompatActivity {
    // Creating DatabaseReference.
    FirebaseDatabase database;
    DatabaseReference myRef ;
    FirebaseUser user;
    private FirebaseAuth auth;
String eventid;
String eventtitle;
Events event;
private TextView show;
    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
   GalleryAdapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<Moments> list ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_gallery);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Intent in = getIntent();
        event= (Events) in.getSerializableExtra("obj");
        eventid = event.getEventID();
        list= new ArrayList<Moments>();
        eventtitle = event.getEventName();
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Moments");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(eventtitle);
        setSupportActionBar(toolbar);
        show = findViewById(R.id.showmessage);
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(EventGallery.this);

// Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Gallery Image.");

// Showing progress dialog.
        progressDialog.show();
        getAllgalleryPhoto();

    }

    public  void  getAllgalleryPhoto() {
       // Toast.makeText(this, "eventis: "+ eventid, Toast.LENGTH_SHORT).show();
        Query budgequery = myRef.orderByChild("eventid").equalTo(eventid);
        budgequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Moments value = d.getValue(Moments.class);
                    list.add(value);
                }
               // Toast.makeText(EventGallery.this, "list size " +list.size(), Toast.LENGTH_SHORT).show();
                if (list.size() > 0) {
                  //  Toast.makeText(EventGallery.this, "list size inside " +list.size(), Toast.LENGTH_SHORT).show();
                   adapter = new GalleryAdapter(list,EventGallery.this);
                    GridLayoutManager recyce = new GridLayoutManager(EventGallery.this,2);
                    /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                    // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                    recyclerView.setLayoutManager(recyce);
                    recyclerView.setItemAnimator( new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);

                   show.setVisibility(View.INVISIBLE);
                    // Hiding the progress dialog.
                    progressDialog.dismiss();


                } else {
                    show.setText("You have no Photo yet. Please upload photo to event");
                    show.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /************************************** Menu Item Stsrt Here ************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem Searcc = menu.findItem(R.id.search);
        MenuItem CelsiusItem = menu.findItem(R.id.tempc);
        MenuItem FahrenheitItem = menu.findItem(R.id.tempf);
        MenuItem EventItem = menu.findItem(R.id.events);
        MenuItem MapItem = menu.findItem(R.id.location_map);
        MenuItem NearPlaceItem = menu.findItem(R.id.nearplace);
        MenuItem MapDirectionItem = menu.findItem(R.id.direction);
        MenuItem WeatherItem = menu.findItem(R.id.weather_info);
        MenuItem LogoutItem = menu.findItem(R.id.logout);
        MenuItem Myprofile = menu.findItem(R.id.profile);
        Searcc.setVisible(false);
        CelsiusItem.setVisible(false);
        FahrenheitItem.setVisible(false);
        EventItem.setVisible(true);
        MapItem.setVisible(true);
        NearPlaceItem.setVisible(true);
        MapDirectionItem.setVisible(true);
        WeatherItem.setVisible(true);
        if(user != null) {
            LogoutItem.setVisible(true);
            Myprofile.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hm:
                //  startActivity(new Intent(WeatherInfo.this,EventList.class));
                break;
            case R.id.events:
                //  startActivity(new Intent(WeatherInfo.this,EventList.class));
                break;
            case R.id.location_map:
                startActivity(new Intent(this,LocationMap.class));
                break;
            case R.id.nearplace:

                startActivity(new Intent(this,NearestPlace.class));
                break;
            case R.id.direction:
                startActivity(new Intent(this,DirectionMap.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this,UserProfile.class));
                break;
            case R.id.weather_info:
                startActivity(new Intent(this,WeatherInfo.class));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /************************************** Menu Item End Here ************************************/
    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (EventGallery.this,MainActivity.class));
                finish();
            }
        });
    }
}
