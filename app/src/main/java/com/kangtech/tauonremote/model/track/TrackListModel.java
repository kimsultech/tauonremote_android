package com.kangtech.tauonremote.model.track;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackListModel {

    @SerializedName("tracks")
    @Expose
    public List<TrackModel> tracks = null;

}
