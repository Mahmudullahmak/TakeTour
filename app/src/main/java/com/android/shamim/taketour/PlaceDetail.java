package com.android.shamim.taketour;

import android.*;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.KeyValueSpinner;
import com.android.shamim.taketour.helper.LocationPreference;
import com.android.shamim.taketour.pojjoclass.HasValueParePlaceType;
import com.android.shamim.taketour.pojjoclass.MyPlace;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceDetail extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private List<MarkerItem> items = new ArrayList<>();
    private ClusterManager<MarkerItem> clusterManager;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private ImageView pphoto;
    FirebaseUser user;
    private FirebaseAuth auth;
    private Spinner ptype, distance;
    private String placeexatType;
    private String placeAreacount;
    double mLatitude = 0;
    double mLongitude = 0;
    FrameLayout mapframe;
    private String viction;
    private String placename;
    private LocationPreference locationPreference;
    Button findBTN;
    private MyPlace myPlace;
    private String placeid;
    private TextView nameTv, addressTv, phoneTv;
private  final static String TAG ="PlaceErrore";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        pphoto = findViewById(R.id.pphoto);
        nameTv = findViewById(R.id.pname);
        addressTv = findViewById(R.id.paddr);
       // phoneTv = findViewById(R.id.pphone);
        mapframe = findViewById(R.id.mapfragment);
       /* GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this *//* FragmentActivity *//*,
                        this *//* OnConnectionFailedListener *//*)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .build();*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Find Nearest Places");
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        client = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Intent intent = getIntent();
        myPlace = (MyPlace) intent.getSerializableExtra("obj");
        if (myPlace != null) {
            placename = myPlace.getPlacename();
            viction = myPlace.getVicinity();
            mLatitude = myPlace.getLat();
            mLongitude = myPlace.getLon();
            placeid = myPlace.getPlaceid();
            nameTv.setText(placename);
            addressTv.setText(viction);
            toolbar.setTitle(placename);
        } else {

            toolbar.setTitle("Place Detail");
        }
        setSupportActionBar(toolbar);
        checkPermission();
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });





     //  Toast.makeText(this, "placeId " +placeid, Toast.LENGTH_SHORT).show();
        getPhotos(placeid);

        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapfragment, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
/*


        Places.GeoDataApi.getPlaceById(googleApiClient, placeid)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override
                    public void onResult(final PlaceBuffer places) {

                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            return;
                        }

                        // Setup Geocoder
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses;

                        // Attempt to Geocode from place lat & long
                        try {

                            addresses = geocoder.getFromLocation(
                                    mLatitude,mLongitude,1);

                            if (addresses.size() > 0) {

                                // Here are some results you can geocode
                                String ZIP;
                                String city;
                                String state;
                                String country;

                                if (addresses.get(0).getPostalCode() != null) {
                                    ZIP = addresses.get(0).getPostalCode();
                                    Log.d("ZIP", ZIP);
                                }

                                if (addresses.get(0).getLocality() != null) {
                                    city = addresses.get(0).getLocality();
                                    Log.d("city", city);
                                }

                                if (addresses.get(0).getAdminArea() != null) {
                                    state = addresses.get(0).getAdminArea();
                                    Log.d("state", state);
                                }

                                if (addresses.get(0).getCountryName() != null) {
                                    country = addresses.get(0).getCountryName();
                                    Log.d("country", country);
                                }
                               */
/* String formatted_address = hPlaceDetails.get("formatted_address");
                                String formatted_phone = hPlaceDetails.get("formatted_phone");
                                String website = hPlaceDetails.get("website");*//*

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        places.release();
                    }
                });

*/

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        checkPermission();
        map.setMyLocationEnabled(true);
        if(map!=null) {
        map.addMarker(new MarkerOptions().title(placename)
                .snippet(viction)
                .position(new LatLng(mLatitude,mLongitude)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude,mLongitude),15));
    }}
    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
    }

    private void getPhotos(String placeId) {
       // final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                if (task.isComplete() && task.isSuccessful()) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    if (photos != null) {
                        // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                         if(photoMetadataBuffer.getCount() > 0) {
                            // Get the first photo in the list.
                            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                            // Get the attribution text.
                            CharSequence attribution = photoMetadata.getAttributions();
                            // Get a full-size bitmap for the photo.
                            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    PlacePhotoResponse photo = task.getResult();
                                    Bitmap bitmap = photo.getBitmap();
                                    pphoto.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void getPlacePhoto(String placeId) {
        mGeoDataClient.getPlacePhotos(placeId).addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                if(task.isComplete() && task.getResult() != null){
                    PlacePhotoMetadataResponse responses = task.getResult();
                    PlacePhotoMetadataBuffer buffer = responses.getPhotoMetadata();
                    if(buffer!=null) {
                        PlacePhotoMetadata metadata = buffer.get(0);
                        mGeoDataClient.getPhoto(metadata).addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                if (task.isComplete() && task.getResult() != null) {
                                    PlacePhotoResponse response = task.getResult();
                                    Bitmap bitmap = response.getBitmap();
                                    pphoto.setImageBitmap(bitmap);
                                } else {

                                }

                            }
                        });

                    }
                }
            }
        });
    }

    public void getPlaces(View view) {

        String stype = ((Spinner)findViewById(R.id.ptype)).getSelectedItem().toString().replaceAll(" ", "_").toLowerCase();
        String area = ((Spinner)findViewById(R.id.distance)).getSelectedItem().toString();
        Toast.makeText(this, "selected type:  "+ stype.replaceAll(" ", "_").toLowerCase(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "selected area:  "+ area.replaceAll(" ", "_").toLowerCase(), Toast.LENGTH_SHORT).show();

        placeexatType =    stype ;  // stype.replaceAll(" ", "_").toLowerCase();
        placeAreacount = area;



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
                startActivity(new Intent (PlaceDetail.this,LoginActivity.class));
                finish();
            }
        });
    }
}

