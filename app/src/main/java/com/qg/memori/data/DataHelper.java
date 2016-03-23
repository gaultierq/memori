package com.qg.memori.data;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Field;

/**
 * Created by q on 01/03/2016.
 */
public class DataHelper {

    public static final String TAG = "DataHelper";

    public static Pair<String, Object> readPK(ModelData o) {
        Field fi = getPkField(o);
        if(fi != null) {
            try {
                return Pair.create(fi.getName(), fi.get(o));
            } catch (IllegalAccessException e) {
                Log.e(TAG, "error while",e);
                return null;
            }
        }
        return null;
    }

    public static void assignPK(Object o, Long value) {
        Field fi = getPkField(o);
        if(fi != null) {
            try {
                fi.set(o, value);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "error while",e);
            }
        }
    }

    @Nullable
    private static Field getPkField(Object o) {
        Field fi = null;
        for (Field f : o.getClass().getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null || !sqlInfo.id()) {
                continue;
            }
            fi = f;
        }
        return fi;
    }

    public static String toString(Object o) {
        if (o instanceof ModelData) {
            StringBuilder b = new StringBuilder();
            for (Field f : o.getClass().getDeclaredFields()) {
                SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
                if (sqlInfo == null) {
                    continue;
                }
                if (b.length() > 0) {
                    b.append(" | ");
                }
                try {
                    b.append(f.getName() + "=" + f.get(o));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return "error";
                }
            }
            return b.toString();
        }
        return null;
    }
}
