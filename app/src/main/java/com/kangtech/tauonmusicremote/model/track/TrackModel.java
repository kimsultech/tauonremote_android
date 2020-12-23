package com.kangtech.tauonmusicremote.model.track;

/*
 * sultannamja - ssx20010531@gmail.com
 * 20201224 03:11
 *
 * Generate by http://www.jsonschema2pojo.org/
 * API source wiki https://github.com/Taiko2k/TauonMusicBox/wiki/Remote-Control-API
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackModel {

    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("artist")
    @Expose
    public String artist;
    @SerializedName("album")
    @Expose
    public String album;
    @SerializedName("album_artist")
    @Expose
    public String albumArtist;
    @SerializedName("duration")
    @Expose
    public Integer duration;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("position")
    @Expose
    public Integer position;
    @SerializedName("album_id")
    @Expose
    public Integer albumId;
    @SerializedName("track_number")
    @Expose
    public String trackNumber;
    @SerializedName("can_download")
    @Expose
    public Boolean canDownload;

}
