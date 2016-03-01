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

    private static final int DATABASE_VERSION = 6;
    public static final String TAG = "sql";
    public static final String DB_NAME = "MEMORI_DB_3";


    public SQLHelper(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(makeCreateCommand(Memory.class));
        db.execSQL(makeCreateCommand(Quizz.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Nullable
    public  <T extends  ModelData> T insertData(T m) {
        Assert.assertNotNull(m);
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv;
        try {
            cv = makeContentValues(m);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Failed to insert", e);
            return null;
        }
        long id = db.insert(m.getClass().getSimpleName(), null, cv);
        if (id >= 0) {
            DataHelper.assignPK(m, id);
        }
        return m;
    }

    public <T extends ModelData> List<T> fetchData(Class<T> clazz) {
        List<T> datas = new ArrayList<T>();

        Set<String> fields = enumDataField(clazz).keySet();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(clazz.getSimpleName(),
                fields.toArray(new String[fields.size()])
                , null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T res = null;
            try {
                res = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
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
    private static ContentValues makeContentValues(ModelData m) throws IllegalAccessException {
        ContentValues v = new ContentValues();
        for (Field f : m.getClass().getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null) {
                continue;
            }
            if (sqlInfo.id()) {
                continue; //smell
            }
            if (f.getType() == String.class) {
                v.put(f.getName(), (String) f.get(m));
            }
            else if (f.getType() == Boolean.TYPE) {
                v.put(f.getName(), (Boolean) f.get(m));
            }
            else if (f.getType() == Date.class) {
                v.put(f.getName(), (Long) f.get(m));
            }
            else if (f.getType() == Integer.TYPE) {
                v.put(f.getName(), (Integer) f.get(m));
            }
            else if (f.getType() == Long.TYPE) {
                v.put(f.getName(), (Long) f.get(m));
            }
            else if (f.getType().isEnum()) {
                Enum e = ((Enum)f.get(m));
                if (e != null) {
                    v.put(f.getName(), e.ordinal());
                }
            }
            else {
                assert false : "not supported";
            }
        }
        return v;
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
            if (f.getType() == String.class) {
                b.append(" string");
            }
            else if (f.getType() == Boolean.TYPE) {
                b.append(" boolean");
            }
            else if (f.getType() == Date.class) {
                b.append(" datetime");
            }
            else if (f.getType() == Integer.TYPE) {
                b.append(" integer");
            }
            else if (f.getType() == Long.TYPE) {
                b.append(" integer");
            }
            else if (f.getType().isEnum()) {
                b.append(" integer");
            }
            else {
                throw new AssertionError("not supported: "+f.getType());
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

                if (f.getType() == String.class) {
                    f.set(res, cursor.getString(i++));

                }
                else if (f.getType() == Boolean.TYPE) {
                    f.set(res, cursor.getInt(i++) != 0);
                }
                else if (f.getType() == Date.class) {
                    f.set(res, new Date(cursor.getInt(i++)));
                }
                else if (f.getType() == Integer.TYPE) {
                    f.set(res, cursor.getInt(i++));
                }
                else if (f.getType() == Long.TYPE) {
                    f.set(res, cursor.getInt(i++));
                }
                else if (f.getType().isEnum()) {
                    int ord = cursor.getInt(i++);
                    f.set(res, f.getType().getEnumConstants()[ord]);
                }
                else {
                    throw new AssertionError("not supported: "+f.getType());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteByPK(Object memory) {
        Pair<String, Object> pk = DataHelper.readPK(memory);
        return getReadableDatabase().delete(memory.getClass().getSimpleName(), pk.first + " = '" + pk.second + "'", null) > 0;
    }

    public static void drop(Context context) {
        boolean res = context.deleteDatabase(DB_NAME);
        Assert.assertTrue(res);
    }
}
