package com.kangtech.tauonremote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerModel {

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("ip")
    @Expose
    public String ip;
    @SerializedName("id")
    @Expose
    public Integer id;

    public ServerModel(Integer id, String ip, String status) {
        this.status = status;
        this.ip = ip;
        this.id = id;
    }
}
