package com.android.shamim.taketour;

import android.*;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.KeyValueSpinner;
import com.android.shamim.taketour.helper.LocationPreference;
import com.android.shamim.taketour.helper.PlaceListAdapter;
import com.android.shamim.taketour.helper.Utility;
import com.android.shamim.taketour.pojjoclass.HasValueParePlaceType;
import com.android.shamim.taketour.pojjoclass.MyPlace;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearestPlace extends AppCompatActivity implements LocationListener {
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private List<MarkerItem>items = new ArrayList<>();
    private ClusterManager<MarkerItem> clusterManager;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private ProgressBar progressBar;
    private  FirebaseUser user;
    private FirebaseAuth auth;
    private Spinner ptype, distance;
    private  String placeexatType;
    private  String placeAreacount;
    double mLatitude=0;
    double mLongitude=0;
    private RecyclerView precyclerView;
    private FrameLayout mapframe;
    private int nextCount = 0;
    private LocationPreference locationPreference;
    private  ArrayList<MyPlace>allplacess;
    PlaceListAdapter placeListAdapter;
    private Button findBTN;
    private TextView show;
    FrameLayout myframe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_place);
        ptype= findViewById(R.id.ptype);
        distance= findViewById(R.id.distance);
        findBTN= findViewById(R.id.findBtn);
        progressBar= findViewById(R.id.progress_bar);
        precyclerView = findViewById(R.id.placeRecyclerView);
        allplacess = new ArrayList<MyPlace>();
        //show.findViewById(R.id.smesess);
       // show.setVisibility(View.INVISIBLE);

        myframe = findViewById(R.id.listcontainer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Find Nearest Places");
        setSupportActionBar(toolbar);
        locationPreference = new LocationPreference(this);
        String latitute= locationPreference.getLaetSaveLatitute();
        String longitute = locationPreference.getLaetSaveLongitute();
        client = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(latitute != null) {
            mLatitude= Double.parseDouble(latitute) ;
        } else {
            mLatitude = 23.777176;
        }
        if(longitute != null) {
            mLongitude= Double.parseDouble(longitute) ;
        } else {
            mLongitude = 90.399452;
        }
        getLastLocation();


        ArrayAdapter<String> typesadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,ContentGenerator.getPlaceType());
        ptype.setAdapter(typesadapter);
        ptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String city = ContentGenerator.generateListOfCities().get(position);
                String city = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,ContentGenerator.generateListOfarea());
        distance.setAdapter(sadapter);
        distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String city = ContentGenerator.generateListOfCities().get(position);
                String city = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void getLastLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()) {
                    lastLocation = task.getResult();
                    if (lastLocation != null) {
                        mLatitude = lastLocation.getLatitude();
                        mLongitude = lastLocation.getLongitude();
                        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    /*map.addMarker(new MarkerOptions().title("My Current Place")
                    .snippet("Karwanbazar")
                    .position(latLng));*/
                        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    } else {
                        LatLng latLng = new LatLng(mLatitude, mLongitude);

                    }
                }
            }
        });
    }


    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(this,NearestPlace.class));
                } else {
                    //code for deny
                }
                break;
        }
    }
    public void getPlaces(View view) {

        String stype = ((Spinner)findViewById(R.id.ptype)).getSelectedItem().toString().replaceAll(" ", "_").toLowerCase();
        String area = ((Spinner)findViewById(R.id.distance)).getSelectedItem().toString();
      //  Toast.makeText(this, "selected type:  "+ stype.replaceAll(" ", "_").toLowerCase(), Toast.LENGTH_SHORT).show();
      //  Toast.makeText(this, "selected area:  "+ area.replaceAll(" ", "_").toLowerCase(), Toast.LENGTH_SHORT).show();

        placeexatType =    stype ;  // stype.replaceAll(" ", "_").toLowerCase();
        placeAreacount = area;
        double rarea =  (1000 * Double.parseDouble(area.substring(0, area.length() - 2)));

        progressBar.setVisibility(View.VISIBLE);
       /* int selectedPosition = mSprPlaceType.getSelectedItemPosition();
        String type = mPlaceType[selectedPosition];*/


        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+mLatitude+","+mLongitude);
        sb.append("&radius="+rarea);
        sb.append("&types="+stype);
        sb.append("&sensor=true");
        sb.append("&key="+ getString(R.string.google_places_api));


        // Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());



    }


    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

       // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      //  mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (NearestPlace.this,LoginActivity.class));
                finish();
            }
        });
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

// Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

// Connecting to url
            urlConnection.connect();

// Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
           // Log.d("Exception while downloading", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
          //  Toast.makeText(NearestPlace.this, "list size "+ list.size(), Toast.LENGTH_SHORT).show();
            allplacess.clear();
if(list.size() > 0) {
            for(int i=0;i<list.size();i++){

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
                allplacess.add(new MyPlace(hmPlace.get("plid"),hmPlace.get("place_name"),hmPlace.get("vicinity"),Double.parseDouble(hmPlace.get("lat")),Double.parseDouble(hmPlace.get("lng"))));

            }
  //  Toast.makeText(NearestPlace.this, "Array size "+ allplacess.size(), Toast.LENGTH_SHORT).show();
showListinList(allplacess);
  //  show.setText("");
  //  show.setEnabled(false);
 //   show.setVisibility(View.INVISIBLE);
} else {
//show.setText("No place found");
//show.setEnabled(true);
//show.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
    progressBar.setEnabled(false);
    Toast.makeText(NearestPlace.this, "No Place Found", Toast.LENGTH_SHORT).show();

}

        }

    }

    private void showListinList(ArrayList<MyPlace> allplacess) {
       // Toast.makeText(this, "outcondition "+allplacess.size(), Toast.LENGTH_SHORT).show();
        if(allplacess.size() > 0) {
           // Toast.makeText(this, "inside " +allplacess.size(), Toast.LENGTH_SHORT).show();
            placeListAdapter = new PlaceListAdapter(NearestPlace.this,allplacess);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NearestPlace.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            precyclerView.setAdapter(placeListAdapter);
            precyclerView.setLayoutManager(linearLayoutManager);
           /* placeListAdapter = new PlaceListAdapter(this, allplacess);
            LinearLayoutManager llm = new LinearLayoutManager(NearestPlace.this);
            //GridLayoutManager glm = new GridLayoutManager(context,1);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            precyclerView.setLayoutManager(llm);
            precyclerView.setAdapter(placeListAdapter);*/
            progressBar.setVisibility(View.GONE);
            progressBar.setEnabled(false);
        }

    }


}

