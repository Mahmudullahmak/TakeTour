package com.android.shamim.taketour;

import android.*;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.LocationPreference;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class LocationMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private List<MarkerItem> items = new ArrayList<>();
    private ClusterManager<MarkerItem> clusterManager;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private LocationPreference locationPreference;
    FirebaseUser user;
    private FirebaseAuth auth;
    private double lat;
    private double lon;
    final int PLACE_PICKER_REQUEST = 1;
    final int PLACE_AUTOCOMPLETE_REQUEST = 2;
    Bundle savedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_location_map);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Map");
        setSupportActionBar(toolbar);
        locationPreference = new LocationPreference(this);
        String latitute= locationPreference.getLaetSaveLatitute();
        String longitute = locationPreference.getLaetSaveLongitute();
        if(latitute != null) {
            lat= Double.parseDouble(latitute) ;
        } else {
            lat = 23.777176;
        }
        if(longitute != null) {
            lon= Double.parseDouble(longitute) ;
        } else {
            lon = 90.399452;
        }

        geoDataClient = Places.getGeoDataClient(this,null);
        placeDetectionClient = Places.getPlaceDetectionClient(this,null);
        client = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
    }
    private void getLastLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()) {
                    lastLocation = task.getResult();

                    if (lastLocation != null) {
                        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        if (latLng != null) {
                            map.addMarker(new MarkerOptions().title("My Current Place")
                                    .position(latLng));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                            locationPreference.saveLocation(String.valueOf(lastLocation.getLatitude()), String.valueOf(lastLocation.getLongitude()));
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 14));
                            map.addMarker(new MarkerOptions().title("My " +
                                    "last Place")
                                    .position(new LatLng(lat, lon)));
                        }
                    } else {
                        LatLng latLng = new LatLng(lat,lon);
                        if (latLng != null) {
                            map.addMarker(new MarkerOptions().title("My Last Place")
                                    .position(latLng));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                           // locationPreference.saveLocation(String.valueOf(lastLocation.getLatitude()), String.valueOf(lastLocation.getLongitude()));
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 14));
                            map.addMarker(new MarkerOptions().title("My " +
                                    "last Place")
                                    .position(new LatLng(lat, lon)));
                        }

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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        checkPermission();
        map.setMyLocationEnabled(true);
        clusterManager = new ClusterManager<MarkerItem>(this,map);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                /*map.addMarker(new MarkerOptions().title("Random")
                        .snippet("Karwanbazar")
                        .position(latLng));*/
                items.add(new MarkerItem(latLng));
                clusterManager.addItems(items);
                clusterManager.cluster();
            }
        });
    }
    public void findCurrentPlaces(final View view) {
        checkPermission();
        placeDetectionClient.getCurrentPlace(null).addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    PlaceLikelihoodBufferResponse responses = task.getResult();
                    int count = responses.getCount();

                    String[]names = new String[count];
                    String[]addresses = new String[count];
                    LatLng[]latLngs = new LatLng[count];

                    for(int i = 0; i < count; i++){
                        PlaceLikelihood likelihood = responses.get(i);
                        names[i] = (String) likelihood.getPlace().getName();
                        addresses[i] = (String) likelihood.getPlace().getAddress();
                        latLngs[i] = likelihood.getPlace().getLatLng();
                        //items.add(new MarkerItem(latLngs[i],names[i],addresses[i]));
                    }
                    /*clusterManager.addItems(items);
                    clusterManager.cluster();*/
                    responses.release();
                    openDialog(names,addresses,latLngs);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationMap.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDialog(final String[] names, final String[] addresses, final LatLng[] latLngs) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng latLng = latLngs[which];
                String address = addresses[which];
                String title = names[which];

                map.clear();
                map.addMarker(new MarkerOptions().position(latLng)
                        .title(title)
                        .snippet(address));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            }
        };
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Pick a place")
                .setItems(names,listener)
                .show();
    }

    private void showAutoCompleteWidget() {
        try {
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setCountry("BD").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(filter).build(this);
            startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void showPlacePickerWidget() {
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(intent,PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PLACE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this,data);
                    LatLng latututelong = place.getLatLng();
                    String placename = String.valueOf(place.getName());
                    map.clear();
                    map.addMarker(new MarkerOptions().position(latututelong)
                            .title(place.getName().toString())
                            .snippet(place.getAddress().toString()));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latututelong,17));

                    Toast.makeText(this, place.getName().toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST:
                if(resultCode == RESULT_OK){
                    Place place = PlaceAutocomplete.getPlace(this,data);
                    LatLng latututelong = place.getLatLng();
                    String placename = String.valueOf(place.getName());
                    map.clear();
                    map.addMarker(new MarkerOptions().position(latututelong)
                            .title(place.getName().toString())
                            .snippet(place.getAddress().toString()));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latututelong,17));

                    //String placeId = place.getId();
                   // String placeId = "ChIJyU3hvb24VTcRKwoamHdlcSw";
                    //getPlacePhoto(placeId);//

                    //Toast.makeText(this, place.getName().toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getPlacePhoto(String placeId) {
        geoDataClient.getPlacePhotos(placeId).addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                if(task.isComplete() && task.getResult() != null){
                    PlacePhotoMetadataResponse responses = task.getResult();
                    PlacePhotoMetadataBuffer buffer = responses.getPhotoMetadata();
                    PlacePhotoMetadata metadata = buffer.get(0);
                    geoDataClient.getPhoto(metadata).addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            if(task.isComplete() && task.getResult() != null){
                                PlacePhotoResponse response = task.getResult();
                                Bitmap bitmap = response.getBitmap();
                                openDialogForImage(bitmap);
                            }
                        }
                    });
                }
            }
        });
    }

    private void openDialogForImage(Bitmap bitmap) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bitmap);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(iv)
                .show();
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
                //startActivity(new Intent(this,LocationMap.class));
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
                startActivity(new Intent (LocationMap.this,LoginActivity.class));
                finish();
            }
        });
    }


    public void pickPlaces(View view) {
        showPlacePickerWidget();

    }

    public void searchPlaces(View view) {
        showAutoCompleteWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()){
            Log.i("DEBUG", "App will Terminate ");
        }else{
            this.onCreate(savedInstanceState);
            Log.i("DEBUG", "Orientation changed");
        }

    }
}
