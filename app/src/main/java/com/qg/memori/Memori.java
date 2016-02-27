package com.qg.memori;

/**
 * Created by q on 27/02/2016.
 */
public class Memori {

    private static Memori instance;

    public static Memori get() {
        return instance == null ? instance = new Memori() : instance;
    }
}
