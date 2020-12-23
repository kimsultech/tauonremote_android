package com.kangtech.tauonmusicremote.model.playlist;

/*
 * sultannamja - ssx20010531@gmail.com
 * 20201224 03:11
 *
 * Generate by http://www.jsonschema2pojo.org/
 * API source wiki https://github.com/Taiko2k/TauonMusicBox/wiki/Remote-Control-API
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistData {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("count")
    @Expose
    public Integer count;

}