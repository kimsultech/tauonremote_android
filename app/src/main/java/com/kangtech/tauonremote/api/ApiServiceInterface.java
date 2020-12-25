package com.kangtech.tauonremote.api;


import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.model.track.TrackModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServiceInterface {

    @GET("/api1/status")
    Observable<StatusModel> getStatus();

    @GET("/api1/trackposition/{playlist}/{position}")
    Observable<TrackModel> getTrack(@Path("playlist") String playlist,
                                    @Path("position") int position);

    @GET("/api1/play")
    Observable<ResponseBody> play();

    @GET("/api1/pause")
    Observable<ResponseBody> pause();

    @GET("/api1/next")
    Observable<ResponseBody> next();

    @GET("/api1/back")
    Observable<ResponseBody> back();

}
