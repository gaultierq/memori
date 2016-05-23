package com.qg.memori.data;

/**
 * Created by q on 23/05/2016.
 */
public enum DbVersion {
    V_1, //stub
    V_2
    ; //introduce Memory.type

    public static int last() {
        DbVersion[] values = DbVersion.values();
        DbVersion v = values[values.length - 1];
        return Integer.valueOf(v.name().split("_")[1]);
    }
}
