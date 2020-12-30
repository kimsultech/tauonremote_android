package com.kangtech.tauonremote.model.status;

/*
 * sultannamja - ssx20010531@gmail.com
 * 20201224 03:11
 *
 * Generate by http://www.jsonschema2pojo.org/
 * API source wiki https://github.com/Taiko2k/TauonMusicBox/wiki/Remote-Control-API
*/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kangtech.tauonremote.model.track.TrackModel;

public class StatusModel {

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("inc")
    @Expose
    public Integer inc;
    @SerializedName("shuffle")
    @Expose
    public Boolean shuffle;
    @SerializedName("repeat")
    @Expose
    public Boolean repeat;
    @SerializedName("progress")
    @Expose
    public Integer progress;
    @SerializedName("volume")
    @Expose
    public Integer volume;
    @SerializedName("playlist")
    @Expose
    public String playlist;
    @SerializedName("playlist_length")
    @Expose
    public Integer playlistLength;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("artist")
    @Expose
    public String artist;
    @SerializedName("album")
    @Expose
    public String album;
    @SerializedName("track")
    @Expose
    public TrackModel track;
    @SerializedName("position")
    @Expose
    public Integer position;
    @SerializedName("album_id")
    @Expose
    public Integer albumId;

}
