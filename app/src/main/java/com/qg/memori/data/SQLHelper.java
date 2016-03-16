package com.qg.memori.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by q on 27/02/2016.
 */

public class SQLHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;
    public static final String TAG = "sql";
    public static final String DB_NAME = "MEMORI_DB_4";
    private Context context;


    public SQLHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static void updateModelByPk(SQLiteDatabase db, ModelData data) {
        Pair<String, Object> pk = DataHelper.readPK(data);
        db.update(data.getClass().getSimpleName(), makeContentValues(data), pk.first + "=" + pk.second, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(makeCreateCommand(Memory.class));
        db.execSQL(makeCreateCommand(Quizz.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "upgrading db : " + oldVersion + " -> " + newVersion);
        drop(context);
    }

    @Nullable
    public  <T extends  ModelData> T insertData(T m) {
        Assert.assertNotNull(m);
        SQLiteDatabase db = getReadableDatabase();
        long id = db.insert(m.getClass().getSimpleName(), null, makeContentValues(m));
        if (id >= 0) {
            DataHelper.assignPK(m, id);
        }
        return m;
    }

    public <T extends ModelData> List<T> fetchSimilar(T obj) {
        StringBuilder selection = new StringBuilder();
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (f.getAnnotation(SqlInfo.class) == null) {
                continue;
            }
            Object v;
            Class<?> t = f.getType();
            try {
                if ((v = f.get(obj)) != null) {
                    if (selection.length() > 0) {
                        selection.append(" and ");
                    }
                    if (t == Boolean.TYPE || t == Boolean.class) {
                        if ((Boolean)v) {
                            selection.append(f.getName() + " = 1");
                        }
                        else {
                            selection.append(f.getName() + " is null or " + f.getName() + " = 0");
                        }
                    }
                    else {
                        selection.append(f.getName() + " = " + v);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fetchData((Class<T>) obj.getClass(), selection.toString());
    }

    public <T extends ModelData> List<T> fetchData(Class<T> clazz, String selection) {
        List<T> datas = new ArrayList<T>();

        Set<String> fields = enumDataField(clazz).keySet();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                clazz.getSimpleName(),
                fields.toArray(new String[fields.size()]),
                selection,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T res = null;
            try {
                res = clazz.newInstance();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (readCursor(cursor, res)) {
                datas.add(res);
            }
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return datas;
    }

    @NonNull
    public static ContentValues makeContentValues(ModelData m) {
        try {
            ContentValues v = new ContentValues();
            for (Field f : m.getClass().getDeclaredFields()) {
                SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
                if (sqlInfo == null) {
                    continue;
                }
                if (sqlInfo.id()) {
                    continue; //smell. FIXME
                }
                Class<?> t = f.getType();
                if (t == String.class) {
                    v.put(f.getName(), (String) f.get(m));
                }
                else if (t == Boolean.TYPE || t == Boolean.class) {
                    v.put(f.getName(), (Boolean) f.get(m));
                }
                else if (t == Date.class) {
                    v.put(f.getName(), (Long) f.get(m));
                }
                else if (t == Integer.TYPE || t == Integer.class) {
                    v.put(f.getName(), (Integer) f.get(m));
                }
                else if (t == Long.TYPE || t == Long.class) {
                    v.put(f.getName(), (Long) f.get(m));
                }
                else if (t.isEnum()) {
                    Enum e = ((Enum) f.get(m));
                    if (e != null) {
                        v.put(f.getName(), e.ordinal());
                    }
                } else {
                    throwFieldNotSupported(f);
                }
            }

            return v;
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    @NonNull
    private static String makeCreateCommand(Class clazz) {
        StringBuilder res = new StringBuilder();
        res.append("CREATE TABLE ").append(clazz.getSimpleName()).append(" (");

        StringBuilder b = new StringBuilder();
        for (Field f : clazz.getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null) {
                continue;
            }
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append(f.getName());
            Class<?> t = f.getType();
            if (t == String.class) {
                b.append(" string");
            }
            else if (t == Boolean.TYPE || t == Boolean.class) {
                b.append(" boolean");
            }
            else if (t == Date.class) {
                b.append(" datetime");
            }
            else if (t == Integer.TYPE || t == Integer.class) {
                b.append(" integer");
            }
            else if (t == Long.TYPE || t == Long.class) {
                b.append(" integer");
            }
            else if (t.isEnum()) {
                b.append(" integer");
            }
            else {
                throwFieldNotSupported(f);
            }
            if (sqlInfo.id()) {
                b.append(" primary key autoincrement");
            }
        }
        res.append(b);
        String s = res.append(");").toString();
        Log.i("sql", "sql create command: " + s);
        return s;
    }

    private static String throwFieldNotSupported(Field f) {
        throw new AssertionError("not supported: "+f.getType());
    }

    private Map<String, Class> enumDataField(Class clazz) {
        Map<String, Class> res = new LinkedHashMap<>();
        for (Field f : clazz.getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null) {
                continue;
            }
            res.put(f.getName(), f.getType());
        }
        return res;
    }

    private boolean readCursor(Cursor cursor, Object res) {
        int i = 0;
        try {
            for (Field f : res.getClass().getDeclaredFields()) {
                SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
                if (sqlInfo == null) {
                    continue;
                }
                Class<?> t = f.getType();
                if (t == String.class) {
                    f.set(res, cursor.getString(i++));
                }
                else if (t == Boolean.TYPE || t == Boolean.class) {
                    f.set(res, cursor.getInt(i++) != 0);
                }
                else if (t == Date.class) {
                    f.set(res, new Date(cursor.getInt(i++)));
                }
                else if (t == Integer.TYPE || t == Integer.class) {
                    f.set(res, cursor.getInt(i++));
                }
                else if (t == Long.TYPE || t == Long.class) {
                    f.set(res, Long.valueOf(cursor.getInt(i++)));
                }
                else if (t.isEnum()) {
                    int ord = cursor.getInt(i++);
                    f.set(res, t.getEnumConstants()[ord]);
                }
                else {
                    throwFieldNotSupported(f);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteByPK(ModelData data) {
        Pair<String, Object> pk = DataHelper.readPK(data);
        return getReadableDatabase().delete(data.getClass().getSimpleName(), pk.first + " = '" + pk.second + "'", null) > 0;
    }

    public static void drop(Context context) {
        boolean res = context.deleteDatabase(DB_NAME);
        Assert.assertTrue(res);
    }
}
