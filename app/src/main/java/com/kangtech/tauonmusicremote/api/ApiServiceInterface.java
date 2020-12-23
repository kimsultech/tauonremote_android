package com.kangtech.tauonmusicremote.api;


import retrofit2.http.GET;

public interface ApiServiceInterface {

    @GET("/radio/getpic")
    Observable<ModelHere> getData();

    @GET("/radio/update_radio")
    Observable<ModelHere> getUpdateData();

}
