package com.android.shamim.taketour;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.MomentAdapter;
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
public class MomentDetail extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef ;
    AlertDialog.Builder  b;
    String eventid;
    String eventtitle;
    Events event;
    private boolean validst = false;
    private TextView show;
    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    MomentAdapter adapter ;
    FirebaseUser user;
    private FirebaseAuth auth;
    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    ArrayList<Moments> list ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_detail);
        Intent in = getIntent();
        event= (Events) in.getSerializableExtra("obj");
        eventid = event.getEventID();
        list= new ArrayList<Moments>();
        eventtitle = event.getEventName();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Moments");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(eventtitle);
        setSupportActionBar(toolbar);
        show = findViewById(R.id.showmessage);
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(MomentDetail.this);

// Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Gallery Image.");

// Showing progress dialog.
        progressDialog.show();
        getAllgalleryPhoto();

    }

    public  void  getAllgalleryPhoto() {
      //  Toast.makeText(this, "eventis: "+ eventid, Toast.LENGTH_SHORT).show();
        Query budgequery = myRef.orderByChild("eventid").equalTo(eventid);
        budgequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Moments value = d.getValue(Moments.class);
                    list.add(value);
                }
                //Toast.makeText(MomentDetail.this, "list size " +list.size(), Toast.LENGTH_SHORT).show();
                if (list.size() > 0) {

                    adapter = new MomentAdapter(list,MomentDetail.this);
                   // GridLayoutManager recyce = new GridLayoutManager(MomentDetail.this,2);
                    LinearLayoutManager recyce = new LinearLayoutManager(MomentDetail.this);
                    recyce.setOrientation(1);

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

    public  void deleteRecord(Moments eve) {
        final String momentid = eve.getMomentsId ();
        b = new AlertDialog.Builder(MomentDetail.this);
        b.setTitle("Delete Moment");
        b.setMessage("Are you sure delete this moment?");
        b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                deleteconfirm(eventid);
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        b.show();
    }
    public void deleteconfirm(String eid)
    {
        myRef.child ( eid ).removeValue ();
    }
    public void editMomentDialog( Moments moment) {
        final Moments mom = moment;
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Caption");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_caption_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText frname  = (EditText) alertLayout.findViewById(R.id.caption);

        Button addfriend = (Button) alertLayout.findViewById(R.id.updateBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);
        frname.setText(mom.getCaptions());

        final AlertDialog ad = builder.create();

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = frname.getText().toString();

                if (fn.length() == 0) {
                    frname.setError("Enter Caption");
                    validst = false;
                } else {
                    validst = true;
                }

                if (validst) {

                    Moments frn = new Moments (mom.getMomentsId(), event.getEventID(),mom.getPhotourl(),fn);
                    myRef.child ( mom.getMomentsId() ).setValue ( frn );
                    Toast.makeText(MomentDetail.this, "Caption Update success", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(EventDetail.this, EventDetail.class));

                    ad.dismiss();
                    ad.cancel();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                ad.cancel();
            }
        });
        ad.show();
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
                startActivity(new Intent(this,EventList.class));
                break;
            case R.id.events:
                startActivity(new Intent(this,EventList.class));
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
                startActivity(new Intent (MomentDetail.this,MainActivity.class));
                finish();
            }
        });
    }
}
