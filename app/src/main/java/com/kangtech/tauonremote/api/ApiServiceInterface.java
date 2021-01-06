package com.kangtech.tauonremote.api;


import com.kangtech.tauonremote.model.lyrics.LyricsModel;
import com.kangtech.tauonremote.model.playlist.PlaylistData;
import com.kangtech.tauonremote.model.playlist.PlaylistModel;
import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.model.track.TrackListModel;
import com.kangtech.tauonremote.model.track.TrackModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServiceInterface {

    @GET("/api1/status")
    Observable<StatusModel> getStatus();

    @GET("/api1/playlists")
    Observable<PlaylistModel> getPlaylist();

    @GET("/api1/tracklist/{playlist}")
    Observable<TrackListModel> getTracklist(@Path("playlist") String playlist);

    @GET("/api1/trackposition/{playlist}/{position}")
    Observable<TrackModel> getTrack(@Path("playlist") String playlist,
                                    @Path("position") int position);

    @GET("/api1/start/{playlist}/{position}")
    Observable<ResponseBody> start(@Path("playlist") String playlist,
                                    @Path("position") int position);

    @GET("/api1/play")
    Observable<ResponseBody> play();

    @GET("/api1/pause")
    Observable<ResponseBody> pause();

    @GET("/api1/next")
    Observable<ResponseBody> next();

    @GET("/api1/back")
    Observable<ResponseBody> back();

    @GET("/api1/shuffle")
    Observable<ResponseBody> shuffle();

    @GET("/api1/repeat")
    Observable<ResponseBody> repeat();

    @GET("/api1/seek1k/{value}")
    Observable<ResponseBody> seek1k(@Path("value") int value);

    @GET("/api1/setvolume/{value}")
    Observable<ResponseBody> setvolume(@Path("value") int value);

    @GET("/api1/lyrics/{trackID}")
    Observable<LyricsModel> getLyrics(@Path("trackID") int trackID);

}
