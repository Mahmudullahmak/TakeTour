package com.android.shamim.taketour;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by BITM Trainer 601 on 1/16/2018.
 */

public interface DirectionService {
    @GET
    Call<DirectionResponse>getDirections(@Url String urlString);
}
