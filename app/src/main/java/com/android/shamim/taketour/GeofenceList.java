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

        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;

        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.shamim.taketour.helper.FriendsAdapter;

        import com.android.shamim.taketour.helper.GeofenceAdapter;
        import com.android.shamim.taketour.pojjoclass.Events;

        import com.android.shamim.taketour.pojjoclass.Friends;
        import com.android.shamim.taketour.pojjoclass.Geofenc;
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

        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;

public class GeofenceList extends AppCompatActivity {

    private TextView show;
    DatabaseReference root;
    private GeofenceAdapter geofenceAdapter;
    public RecyclerView mRecyclerView;
    FirebaseUser user;
    private FirebaseAuth auth;
    private Events event;
    private FloatingActionButton fab;
    private  AlertDialog.Builder b;
    ArrayList<Geofenc> geofencs = new ArrayList <Geofenc>( );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_list);

        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        show = findViewById(R.id.showmessage);
        //  FirebaseDatabase.getInstance ().setPersistenceEnabled ( true );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        event= (Events) intent.getSerializableExtra("obj");
        root = FirebaseDatabase.getInstance ().getReference ("Geofence");
        root.keepSynced ( true );
        toolbar.setTitle(event.getEventName()+ " Geofence List");
        setSupportActionBar(toolbar);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String eventTitle = intent.getStringExtra(SearchManager.QUERY);
            //call api using city name
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(this,
                            CityNameSuggestions.AUTHORITY,
                            CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(eventTitle, null);
            // Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
            // getSearchEventList(eventTitle);
            //getCurentWeatherinformationByArea(cityName);
        } else {
            //getAllGeofenceList();
        }




        getAllGeofenceList();

    }
    public  void  getAllGeofenceList() {
        Query budgequery = root.orderByChild( "eventid" ).equalTo(event.getEventID());
        budgequery.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                geofencs.clear ();
                for (DataSnapshot d: dataSnapshot.getChildren ()){
                    Geofenc ev= d.getValue (Geofenc.class);
                    geofencs.add ( ev );

                }

                if(geofencs.size() > 0){
                    Toast.makeText(GeofenceList.this, "array size: " + geofencs.size(), Toast.LENGTH_SHORT).show();
                    geofenceAdapter = new GeofenceAdapter(GeofenceList.this ,geofencs);
                    LinearLayoutManager llm = new LinearLayoutManager(GeofenceList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(geofenceAdapter);
                    show.setVisibility(View.INVISIBLE);
                } else {
                    show.setText("Your geofence list is empty. Please add Geofence");
                    show.setVisibility(View.VISIBLE);
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
                startActivity(new Intent(GeofenceList.this,AddGeofencing.class).putExtra("obj",event));
            }
        });


    }

    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (GeofenceList.this,MainActivity.class));
                finish();
            }
        });
    }

    public void gotoweather(View view) {
        startActivity(new Intent (GeofenceList.this,WeatherInfo.class));
        finish();
    }
    public  void deleteRecord(Geofenc eve) {
        final String exid = eve.getFencid ();
        b = new AlertDialog.Builder(GeofenceList.this);
        b.setTitle("Remove Geofence");
        b.setMessage("Are you sure remove this geofence?");
        b.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                deleteconfirm(exid);
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

}
