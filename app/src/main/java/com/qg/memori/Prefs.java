package com.qg.memori;

/**
 * Created by q on 23/05/2016.
 */
public enum Prefs {
    APP_PLATFORM("PROD");

    public final String def;

    Prefs(String def) {
        this.def = def;
    }
}
