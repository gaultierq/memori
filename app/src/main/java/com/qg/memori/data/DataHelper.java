package com.qg.memori.data;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    public static Bundle putInBundle(Bundle bundle, ModelData v) {
        bundle.putSerializable(v.getClass().getSimpleName(), v);
        return bundle;
    }

    public static <T extends ModelData> Bundle createBundleFromList(List<T> dataList) {
        Bundle res = new Bundle();
        putListInBundle(res, dataList);
        return res;
    }

    public static <T extends ModelData> Bundle putListInBundle(Bundle bundle, List<T> dataList) {
        if (dataList != null) {
            T t = null;
            for (int i = 0; i < dataList.size(); i++) {
                if ((t = dataList.get(i)) != null) {
                    break;
                }
            }
            if (t != null) {
                bundle.putSerializable(t.getClass().getSimpleName() + "_list", new ArrayList<T>(dataList));
            }
        }
        return bundle;
    }

    @Nullable
    public static <T extends ModelData> T readData(Bundle b, Class<T> modelToRead) {
        return modelToRead.cast(b.getSerializable(modelToRead.getSimpleName()));
    }

    @Nullable
    public static <T extends ModelData> ArrayList<T> readDataList(Bundle b, Class<T> modelToRead) {
        return ArrayList.class.cast(b.getSerializable(modelToRead.getSimpleName() + "_list"));
    }
}
