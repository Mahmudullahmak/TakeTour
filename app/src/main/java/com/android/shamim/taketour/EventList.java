package com.android.shamim.taketour;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.EventAdapter;
import com.android.shamim.taketour.helper.WeatherAdapter;
import com.android.shamim.taketour.pojjoclass.Events;
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

public class EventList extends AppCompatActivity {
private EditText eventNameET, eventBudgetET, eventDateTV;
private TextView show;
ListView eventList;
DatabaseReference root;
private EventAdapter eventAdapter;
public RecyclerView mRecyclerView;
FirebaseUser user;
private FirebaseAuth auth;
private Button saveBt;
private FloatingActionButton fab;
public String updatedeventid = null;
private  AlertDialog.Builder b;
ProgressBar progressBar;
ArrayList<Events> events = new ArrayList <Events>( );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Event List");
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        progressBar = findViewById(R.id.eventloader);
        progressBar.setVisibility(View.VISIBLE);
        show = findViewById(R.id.showmessage);
      //  FirebaseDatabase.getInstance ().setPersistenceEnabled ( true );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        root =FirebaseDatabase.getInstance ().getReference ("Events");
        root.keepSynced ( true );
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String eventTitle = intent.getStringExtra(SearchManager.QUERY);
            //call api using city name
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(this,
                            CityNameSuggestions.AUTHORITY,
                            CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(eventTitle, null);
            // Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
            getSearchEventList(eventTitle);
            //getCurentWeatherinformationByArea(cityName);
        } else {
            getAllEventList();
        }






    }
public  void  getAllEventList() {
    Query budgequery = root.orderByChild( "userID" ).equalTo(user.getUid());
    budgequery.addValueEventListener ( new ValueEventListener () {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            events.clear ();
            for (DataSnapshot d: dataSnapshot.getChildren ()){
                Events ev= d.getValue (Events.class);
                events.add ( ev );
            }
            if(events.size() > 0){
                eventAdapter = new EventAdapter(EventList.this ,events);
                LinearLayoutManager llm = new LinearLayoutManager(EventList.this);
                //GridLayoutManager glm = new GridLayoutManager(context,1);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(eventAdapter);
                show.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
                progressBar.setEnabled(false);
            } else {
                show.setText("You have no event yet. Please create event");
                show.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                progressBar.setEnabled(false);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    } );
    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(EventList.this, AddEvent.class));

        }
    });


}
    public  void  getSearchEventList(String ename) {
        Query budgequery = root.orderByChild( "userID" ).equalTo(user.getUid());
        budgequery.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear ();
                for (DataSnapshot d: dataSnapshot.getChildren ()){
                    Events ev= d.getValue (Events.class);
                    events.add ( ev );
                }
                if(events.size() > 0){
                    progressBar.setVisibility(View.GONE);
                    progressBar.setEnabled(false);
                    eventAdapter = new EventAdapter(EventList.this ,events);
                    LinearLayoutManager llm = new LinearLayoutManager(EventList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(eventAdapter);
                    show.setVisibility(View.INVISIBLE);

                } else {
                    show.setText("No result found ");
                    show.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventList.this, AddEvent.class));

            }
        });


    }
    public void saveEvent(View view) {
        String ename = eventNameET.getText().toString();
        String eventdate = eventDateTV.getText().toString();
        double budget = Double.parseDouble (eventBudgetET.getText().toString());
        String eventId = root.push().getKey();
        String userid = user.getUid ();
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        switch (buttonText){
            case "Save":
                Events ev = new Events ( eventId,userid,ename,budget, eventdate);
                root.child ( eventId ).setValue ( ev );
                break;
            case "Update":
                Events evu = new Events ( updatedeventid,userid,ename,budget, eventdate);
                root.child ( updatedeventid ).setValue ( evu );
                saveBt.setText ( "Save" );
                break;

        }


    }
    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener( new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (EventList.this,MainActivity.class));
                finish();
            }
        });
    }

    public void gotoweather(View view) {
        startActivity(new Intent (EventList.this,WeatherInfo.class));
        finish();
    }
    public  void deleteRecord(Events eve) {
       final String eventid = eve.getEventID ();
        b = new AlertDialog.Builder(EventList.this);
        b.setTitle("Delete Event");
        b.setMessage("Are you sure delete this event?");
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
       root.child ( eid ).removeValue ();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("ⓘ Exit ! From " + getString(R.string.app_name));
        alertDialogBuilder
                .setMessage(Html.fromHtml("<p style='text-align:center;'>Please Chose what do you want </p><h3 style='text-align:center;'>Click Yes to Exit !</h4>"))
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);

                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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
                startActivity(new Intent(EventList.this,LocationMap.class));
                break;
            case R.id.nearplace:

                startActivity(new Intent(EventList.this,NearestPlace.class));
                break;
            case R.id.direction:
                startActivity(new Intent(EventList.this,DirectionMap.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this,UserProfile.class));
                break;
            case R.id.weather_info:
                startActivity(new Intent(EventList.this,WeatherInfo.class));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /************************************** Menu Item End Here ************************************/

}
