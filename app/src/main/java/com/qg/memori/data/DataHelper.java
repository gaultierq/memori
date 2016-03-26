package com.qg.memori.data;

import com.j256.ormlite.field.DatabaseField;

import java.lang.reflect.Field;

/**
 * Created by q on 01/03/2016.
 */
public class DataHelper {

    public static String toString(Object o) {
        if (o instanceof ModelData) {
            StringBuilder b = new StringBuilder();
            for (Field f : o.getClass().getDeclaredFields()) {
                DatabaseField sqlInfo = f.getAnnotation(DatabaseField.class);
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
