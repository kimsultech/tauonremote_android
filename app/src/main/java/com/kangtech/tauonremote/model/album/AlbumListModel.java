package com.kangtech.tauonremote.model.album;

/*
 * sultannamja - ssx20010531@gmail.com
 * 20200114 02:38
 *
 * Generate by http://www.jsonschema2pojo.org/
 * API source wiki https://github.com/Taiko2k/TauonMusicBox/wiki/Remote-Control-API
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumListModel {

    @SerializedName("albums")
    @Expose
    public List<AlbumModel> albums = null;
}
