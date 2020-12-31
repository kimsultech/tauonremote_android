package com.kangtech.tauonremote.model.lyrics;

/*
 * sultannamja - ssx20010531@gmail.com
 * 20201224 03:11
 *
 * Generate by http://www.jsonschema2pojo.org/
 * API source wiki https://github.com/Taiko2k/TauonMusicBox/wiki/Remote-Control-API
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LyricsModel {

    @SerializedName("track_id")
    @Expose
    public Integer trackId;
    @SerializedName("lyrics_text")
    @Expose
    public String lyricsText;

}
