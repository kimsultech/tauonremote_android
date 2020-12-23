package com.kangtech.tauonmusicremote.util;


import android.app.Application;

import com.kangtech.tauonmusicremote.api.ApiServiceInterface;
import com.kangtech.tauonmusicremote.api.RetrofitClient;

public class Server extends Application {

        static String ip = SharedPreferencesUtils.getString("ip", "127.0.0.1");
        static String port = SharedPreferencesUtils.getString("port", "7590");

        public final static String BASE_URL = ip;



        public static ApiServiceInterface getApiServiceInterface() {
                return RetrofitClient.getClient("http://" + BASE_URL + ":" + port + "/").create(ApiServiceInterface.class);
        }
}
