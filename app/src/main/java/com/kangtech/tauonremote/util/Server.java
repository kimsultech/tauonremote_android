package com.kangtech.tauonremote.util;


import android.app.Application;

import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.api.RetrofitClient;

public class Server extends Application {

        /*static String ip = SharedPreferencesUtils.getString("ip", "127.0.0.1");
        static String port = SharedPreferencesUtils.getString("port", "7590");*/

        public final static String BASE_URL = SharedPreferencesUtils.getString("ip", "127.0.0.1");



        public static ApiServiceInterface getApiServiceInterface() {
                return RetrofitClient.getClient("http://" + BASE_URL + ":" + 7814 + "/").create(ApiServiceInterface.class);
        }

}
