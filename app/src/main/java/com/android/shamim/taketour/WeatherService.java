package com.android.shamim.taketour;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by tmshamim on 1/1/2018.
 */

public interface WeatherService {

    /*@GET("weather?")
    Call<CurrentWeatherResponse>getCurrentWeather(@QueryMap Map<String,String>options);*/


    @GET()
    Call<CurrentWeatherResponse>getCurrentWeather(@Url String urlString);
    @GET()
    Call<ForecastWeatherResponse>getForecastWeather(@Url String urlString);
}
