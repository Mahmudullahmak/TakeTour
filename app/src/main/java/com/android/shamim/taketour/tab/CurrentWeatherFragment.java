package com.android.shamim.taketour.tab;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.CurrentWeatherResponse;
import com.android.shamim.taketour.R;
import com.android.shamim.taketour.WeatherInfo;
import com.android.shamim.taketour.WeatherService;
import com.android.shamim.taketour.helper.LocationPreference;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentWeatherFragment extends Fragment {
     TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, updatedField, maxTemp, minTemp, sunRise, sunSet;
    ImageView weatherIcon;
       public List<CurrentWeatherResponse.Weather> weathers = null;
    public String cunit;
    public int wcount;
        public ProgressBar progressBar;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    public double lat ;
    public   double lon ;
private  String mess;
private String cityname;
    LocationPreference locationPreference;
    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

      public static CurrentWeatherFragment newInstance(Bundle cb) {
        CurrentWeatherFragment cfragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args = cb;
        cfragment.setArguments(args);
        return cfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cunit = getArguments().getString("unit");
            wcount = getArguments().getInt("foreCastCount");
            lon = getArguments().getDouble("lon");
            lat = getArguments().getDouble("lat");
            mess= getArguments().getString("mess");
            cityname = getArguments().getString("cityname");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_current_weather, container, false);
       // String getArgument = getArguments().getString("unit");
        locationPreference = new LocationPreference(getContext());
        cityField = (TextView) fragmentView.findViewById(R.id.city_field);
        updatedField = (TextView) fragmentView.findViewById(R.id.updated_field);
        detailsField = (TextView) fragmentView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) fragmentView.findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) fragmentView.findViewById(R.id.humidity_field);
        pressure_field = (TextView) fragmentView.findViewById(R.id.pressure_field);
        weatherIcon = (ImageView) fragmentView.findViewById(R.id.weather_icon);
        maxTemp = (TextView) fragmentView.findViewById(R.id.maxTemp);
        minTemp = (TextView) fragmentView.findViewById(R.id.minTemp);
        sunRise = (TextView) fragmentView.findViewById(R.id.sunrise);
        sunSet = (TextView) fragmentView.findViewById(R.id.sunset);
       // progressBar = (ProgressBar) fragmentView.findViewById(R.id.currentprogress);
        //progressBar.setVisibility(View.VISIBLE);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        boolean connected =  checkInternetConnection();
        if(connected) {

            if (cityname == null) {
                getCurrentLocation();
            } else {
                getLatLonByCity(cityname);
            }
        } else {
            cityField.setText("Please check your internet connection");
        }

        return fragmentView;

    }
    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
    }
    private void getCurrentLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    lastLocation = task.getResult();
                    lat= lastLocation.getLatitude();
                    lon= lastLocation.getLongitude();
                    locationPreference.saveLocation(String.valueOf(lastLocation.getLatitude()),String.valueOf(lastLocation.getLongitude()));
                    //  Toast.makeText(WeatherInfo.this ,"size:----  "+lastLocation.getLatitude() ,Toast.LENGTH_LONG).show();
                    getCurentWeatherinformationBylatLong(lastLocation.getLatitude() ,lastLocation.getLongitude());
                }
            }
        });
    }
    public  void  getLatLonByCity( String city) {

        if(Geocoder.isPresent()){
            try {
                //  String location = "theNameOfTheLocation";
                Geocoder gc = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses= gc.getFromLocationName( city, 1); // get the found Address Objects
                if(addresses.size() >0) {
                    if(addresses.get(0).hasLatitude() && addresses.get(0).hasLongitude()) {
                        lat = addresses.get(0).getLatitude();
                        lon = addresses.get(0).getLongitude();
                        getCurentWeatherinformationBylatLong(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                    }
                } else {
                    mess = "Invalid city name";
                    cityField.setText(mess);
                }
                // Toast.makeText(WeatherInfo.this ,"size:----  "+addresses.get(0).getLatitude() ,Toast.LENGTH_LONG).show();



               /* List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){

                        lat = a.getLatitude();
                        lon = a.getLongitude();

                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }*/
            } catch (IOException e) {
                //Toast.makeText(WeatherInfo.this ,"size:----  "+e.getMessage() ,Toast.LENGTH_LONG).show();

                // handle the exception
            }
        }


    }
    /**************************Call Weather API for curent weather**************************/
    public void getCurentWeatherinformationBylatLong( double l, double ln){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.weather_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service =  retrofit.create(WeatherService.class);
        String urlString = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",l,ln,cunit,getResources().getString(R.string.weather_api));
        Call<CurrentWeatherResponse> call = service.getCurrentWeather(urlString);
        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if(response.code() == 200){
                    CurrentWeatherResponse currentWeatherResponse = response.body();
                   // Toast.makeText(getActivity() ,"responsecode:  "+response.code() ,Toast.LENGTH_LONG).show();


                    weathers = currentWeatherResponse.getWeather();
                    DateFormat df = DateFormat.getDateTimeInstance();
                    String city = currentWeatherResponse.getName().toUpperCase(Locale.US) + ", " + currentWeatherResponse.getSys().getCountry();
                    String temperature = String.valueOf(currentWeatherResponse.getMain().getTemp())+ "° " + getdegree();
                    String tdate = df.format(new Date(currentWeatherResponse.getDt()*1000));
                    String mintemp =  "Min Temp: "+ String.valueOf(currentWeatherResponse.getMain().getTempMin())+"° " + getdegree();
                    String maxtemp =  "Max Temp: "+String.valueOf(currentWeatherResponse.getMain().getTempMax())+"° " + getdegree();
                    String icons =  weathers.get(0).getIcon() +".png";
                    String humanity =  "Humidity: "+String.valueOf(currentWeatherResponse.getMain().getHumidity())+" %";
                    String preser =  "Pressure: "+String.valueOf(currentWeatherResponse.getMain().getPressure())+ " mb";
                    String description =  weathers.get(0).getDescription();
                    String sunrise =  "Sun Rise: "+convertLongToTime( currentWeatherResponse.getSys().getSunrise());
                    String sunset =  "Sun Set: "+convertLongToTime( currentWeatherResponse.getSys().getSunset());

                    cityField.setText(city);
                    updatedField.setText(tdate);
                    detailsField.setText(description);
                    currentTemperatureField.setText( temperature);
                    humidity_field.setText(humanity);
                    pressure_field.setText(preser);
                    maxTemp.setText(maxtemp);
                    minTemp.setText(mintemp);
                    sunRise.setText(sunrise);
                    sunSet.setText(sunset);
                    Picasso.with(getActivity()).load(getResources().getString(R.string.weather_image_url)+icons).into(weatherIcon);

//progressBar.setVisibility(View.INVISIBLE);

                }
            }
            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                boolean connected =  checkInternetConnection();
                if(connected) {
                    cityField.setText(t.getMessage());
                } else {
                    cityField.setText("Please check your internet connection");
                }
            }
        });
    }
    public  String getdegree() {

        String showdegree = "";
        switch (cunit) {
            case "metric":
                showdegree = "C";
                break;
            case "imperial":
                showdegree = "F";
                break;
        }
        return  showdegree;
    }
    public  String convertLongToTime(long milliseconds) /* This is your topStory.getTime()*1000 */ {
        DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }
   public boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else {
            return false;
        }

    }
}