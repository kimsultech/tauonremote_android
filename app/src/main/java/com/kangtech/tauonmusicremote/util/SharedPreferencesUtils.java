package com.kangtech.tauonmusicremote.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    private SharedPreferencesUtils()
    {

    }

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("tauon_remote", Activity.MODE_PRIVATE);
            editor = prefs.edit();
        }
    }
    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }



    public static String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public static Boolean getBoolean(String key, Boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }
}
