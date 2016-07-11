package com.qg.memori.data;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by q on 01/03/2016.
 */
public class DataHelper {

    public static String toString(Object o) {
        if (o instanceof ModelData) {
            StringBuilder b = new StringBuilder();
            for (Field f : o.getClass().getDeclaredFields()) {
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

    public static Map<String, Object> introspect(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        for(Field f : fields) {
            if (f.getAnnotation(Exclude.class) == null) {
                try {
                    map.put(f.getName(), f.get(obj));
                } catch (IllegalAccessException e) {
                    Logger.e("", e);
                }
            }
        }
        return map;
    }
}
