package com.kangtech.tauonremote.api;


import com.kangtech.tauonremote.model.status.StatusModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiServiceInterface {

    @GET("/api1/status")
    Observable<StatusModel> getStatus();


}
