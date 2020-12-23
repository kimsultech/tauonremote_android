package com.kangtech.tauonmusicremote.util;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class AppSingleton extends MultiDexApplication {
    private static final String TAG ="AppSingleton" ;


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtils.init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
