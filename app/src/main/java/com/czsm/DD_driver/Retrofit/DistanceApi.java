package com.czsm.DD_driver.Retrofit;

import com.czsm.DD_driver.model.TimeModelClass;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by macbook on 04/08/16.
 */
public interface DistanceApi{

    @GET
    Call<TimeModelClass> getUsers(@Url String url);

}