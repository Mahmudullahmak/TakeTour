package com.android.shamim.taketour;
        import android.Manifest;
        import android.app.SearchManager;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.location.Location;
        import android.location.LocationListener;
        import android.os.Build;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.SearchView;
        import android.support.v7.widget.Toolbar;
        import android.text.Html;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import com.android.shamim.taketour.helper.LocationPreference;
        import com.firebase.ui.auth.AuthUI;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.GoogleMapOptions;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.Polyline;
        import com.google.android.gms.maps.model.PolylineOptions;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

        import java.util.ArrayList;
        import java.util.List;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionMap extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener, LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener{

    private GoogleMap map;
    private GoogleMapOptions options;
    private DirectionService service;
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private String[]instructions;
    private Button btnIns;
    private Button btnNextRoute;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;
    double end_latitude, end_longitude;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private int routeCount = 0;
    private int nextCount = 0;
    private  LocationPreference locationPreference;
    private double clat;
    private  double clon;
    private  double slat;
    private  double slon;
    private double elat;
    private double elon;
    private  String origin;
    private  String destination;
    ArrayList<LatLng> listPoints;
    FirebaseUser user;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Get Direction on Map");
        setSupportActionBar(toolbar);
        btnIns = findViewById(R.id.btnInstructions);
        btnNextRoute = findViewById(R.id.btnNext);
        locationPreference = new LocationPreference(this);
        String latitute= locationPreference.getLaetSaveLatitute();
        String longitute = locationPreference.getLaetSaveLongitute();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(latitute != null) {
            clat= Double.parseDouble(latitute) ;
        } else {
            clat = 23.777176;
        }
        if(longitute != null) {
            clon= Double.parseDouble(longitute) ;
        } else {
            clon = 90.399452;
        }
        listPoints = new ArrayList<LatLng>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(DirectionService.class);
        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer,mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

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
        buildGoogleApiClient();
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(clat,clon), 14));
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
               // map.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }*/

        map.setOnPolylineClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnMarkerClickListener(this);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    map.clear();
                    btnIns.setEnabled(false);
                    btnNextRoute.setEnabled(false);
                }
                //Save first point select


                if (listPoints.size() == 0) {
                    slat = latLng.latitude;
                    slon = latLng.longitude;
                    origin = String.valueOf(slat) + ", " + String.valueOf(slon);
                   // Toast.makeText(DirectionMap.this, "origin " +origin , Toast.LENGTH_SHORT).show();

                }
                if (listPoints.size() == 1) {
                    elat = latLng.latitude;
                    elon = latLng.longitude;
                    destination = String.valueOf(elat) + ", " + String.valueOf(elon);
                   // Toast.makeText(DirectionMap.this, "origin " +destination , Toast.LENGTH_SHORT).show();


                }
                listPoints.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.addMarker(markerOptions);

            }
        });
      //  getDirections();
        /*LatLng start = new LatLng(23.750424, 90.393389);
        LatLng end = new LatLng(23.751272, 90.387432);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start,14));

        Polyline polyline = map.addPolyline(new PolylineOptions()
        .add(start)
        .add(end)
        .clickable(true));
        polyline.setTag("test");*/
    }

    private void getDirections() {
        String key = getString(R.string.google_dierction_api);
       // String origin = "23.750538,90.393564";
       // String destination = "23.824152,90.367718";
        //Toast.makeText(this, "Location : "+ origin + " kothay " +destination ,  Toast.LENGTH_SHORT).show();
        String urlString = String.format("directions/json?origin=%s&destination=%s&alternatives=true&key=%s",origin,destination,key);

        Call<DirectionResponse> directionResponseCall = service.getDirections(urlString);
        directionResponseCall.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if(response.code() == 200){
                    btnIns.setEnabled(true);
                    processData(response);
                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    private void processData(Response<DirectionResponse> response) {
        DirectionResponse directionResponse = response.body();
        routeCount = directionResponse.getRoutes().size();
        if(routeCount > 1) {
            btnNextRoute.setEnabled(true);
        } else {
            btnNextRoute.setEnabled(false);
        }
        List<DirectionResponse.Step>steps = directionResponse.getRoutes().get(nextCount)
                .getLegs().get(0).getSteps();
        instructions = new String[steps.size()];

        double camLat = directionResponse.getRoutes().get(nextCount).getLegs().get(0).getStartLocation().getLat();
        double camlng = directionResponse.getRoutes().get(nextCount).getLegs().get(0).getStartLocation().getLng();
        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(camLat,camlng),14));

        for(int i = 0; i < steps.size(); i++){
            double latStart = steps.get(i).getStartLocation().getLat();
            double lngStart = steps.get(i).getStartLocation().getLng();
            double latEnd = steps.get(i).getEndLocation().getLat();
            double lngEnd = steps.get(i).getEndLocation().getLng();
            String instruction = String.valueOf(Html.fromHtml(steps.get(i).getHtmlInstructions()));
            instructions[i] = instruction;
            LatLng startPoint = new LatLng(latStart,lngStart);
            LatLng endPoint = new LatLng(latEnd,lngEnd);

            Polyline polyline = map.addPolyline(new PolylineOptions()
                    .add(startPoint)
                    .add(endPoint)
                    .clickable(true).width(15).color(Color.RED).geodesic(true));
            polyline.setTag(instruction);
        }
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(new LatLng(elat,elon));
        map.addMarker(markerOptions2) ;
        MarkerOptions moption = new MarkerOptions();
        moption.position(new LatLng(slat,slon));
        map.addMarker(moption);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        Toast.makeText(this, polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    public void showInstructions(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setItems(instructions,null)
                .show();
    }

    public void nextRoute(View view) {
        if(nextCount < routeCount){
            getDirections();
            nextCount++;
            if(nextCount == routeCount){
                nextCount = 0;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = map.addMarker(markerOptions);

        //move map camera
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(14));


        Toast.makeText(DirectionMap.this,"Your Current Location", Toast.LENGTH_LONG).show();


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getmyDirections(View view) {

        getDirections();
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
                startActivity(new Intent (DirectionMap.this,LoginActivity.class));
                finish();
            }
        });
    }

}
