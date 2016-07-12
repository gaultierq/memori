package com.qg.memori;

/**
 * Created by q on 23/05/2016.
 */
public enum Prefs {
    APP_PLATFORM("PROD"),
    WORKOUT_TIME("09:00")
    ;

    public final String defaultValue;

    Prefs(String def) {
        this.defaultValue = def;
    }
}
