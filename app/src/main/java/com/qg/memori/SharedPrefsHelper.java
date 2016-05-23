package com.qg.memori;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by q on 23/05/2016.
 */
public class SharedPrefsHelper {

    public static String read(Context context, Prefs pref) {
        String[] s = pref.name().split("_");
        SharedPreferences sharedPreferences = context.getSharedPreferences(s[0], Context.MODE_PRIVATE);
        return sharedPreferences.getString(s[1], pref.def);
    }

    public static void write(Context context, Prefs pref, String val) {
        String[] s = pref.name().split("_");
        SharedPreferences sharedPreferences = context.getSharedPreferences(s[0], Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(s[1], val);
        e.commit();
    }

}
